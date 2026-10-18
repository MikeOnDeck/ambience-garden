#!/bin/bash
set -euo pipefail

# Fetch sound assets for Charmonium build
# Primary: svenhjol/charmony-ambient-sounds (public)
# Fallback: MikeOnDeck/charmony-sounds-archive (private, requires SOUNDS_ARCHIVE_TOKEN)

SOUNDS_DIR="mods/ambient_sounds/src/main/resources/assets/charmony/sounds"
UPSTREAM_REPO="https://github.com/svenhjol/charmony-ambient-sounds.git"
UPSTREAM_COMMIT="02d2e4289517f0c0ae82e7d1729b3f71ef5e0267"
FALLBACK_REPO="https://github.com/MikeOnDeck/charmony-sounds-archive.git"
EXPECTED_MIN_FILES=100

echo "Fetching sound assets..."

# Clean any existing sound folders
rm -rf "$SOUNDS_DIR/biomes" "$SOUNDS_DIR/environment"
mkdir -p "$SOUNDS_DIR"

# Try upstream public repo first
if git clone --depth 1 "$UPSTREAM_REPO" /tmp/charmony-sounds 2>/dev/null; then
    echo "✓ Cloned public upstream repo"
    cd /tmp/charmony-sounds
    
    # Check out the pinned commit
    git fetch --depth 1 origin "$UPSTREAM_COMMIT" 2>/dev/null || {
        echo "Warning: Could not fetch specific commit, using default branch"
    }
    git checkout "$UPSTREAM_COMMIT" 2>/dev/null || {
        echo "Warning: Could not checkout commit $UPSTREAM_COMMIT, using HEAD"
    }
    
    cd - > /dev/null
    
    # Copy sound folders (they're in src/main/resources/assets/charmony/sounds/)
    UPSTREAM_SOUNDS="/tmp/charmony-sounds/src/main/resources/assets/charmony/sounds"
    if [ -d "$UPSTREAM_SOUNDS/biomes" ] && [ -d "$UPSTREAM_SOUNDS/environment" ]; then
        cp -r "$UPSTREAM_SOUNDS/biomes" "$SOUNDS_DIR/"
        cp -r "$UPSTREAM_SOUNDS/environment" "$SOUNDS_DIR/"
        rm -rf /tmp/charmony-sounds
        echo "✓ Copied sounds from upstream"
    else
        rm -rf /tmp/charmony-sounds
        echo "✗ Expected folders not found in upstream repo"
        echo "  Looked in: $UPSTREAM_SOUNDS"
        exit 1
    fi
else
    echo "✗ Could not clone public upstream, trying fallback..."
    
    # Try private fallback repo with token
    if [ -z "${SOUNDS_ARCHIVE_TOKEN:-}" ]; then
        echo "✗ SOUNDS_ARCHIVE_TOKEN not set"
        echo "  To use the fallback repo, create a GitHub personal access token"
        echo "  with 'contents:read' scope for MikeOnDeck/charmony-sounds-archive"
        echo "  and set it as the SOUNDS_ARCHIVE_TOKEN secret/environment variable."
        exit 1
    fi
    
    FALLBACK_URL="https://x-access-token:${SOUNDS_ARCHIVE_TOKEN}@github.com/MikeOnDeck/charmony-sounds-archive.git"
    
    if git clone --depth 1 "$FALLBACK_URL" /tmp/charmony-sounds-fallback 2>/dev/null; then
        echo "✓ Cloned private fallback repo"
        
        # The fallback repo layout may vary, check common paths
        FALLBACK_SRC="/tmp/charmony-sounds-fallback"
        
        # Try sounds/biomes and sounds/environment first
        if [ -d "$FALLBACK_SRC/sounds/biomes" ] && [ -d "$FALLBACK_SRC/sounds/environment" ]; then
            cp -r "$FALLBACK_SRC/sounds/biomes" "$SOUNDS_DIR/"
            cp -r "$FALLBACK_SRC/sounds/environment" "$SOUNDS_DIR/"
        # Try root-level biomes and environment
        elif [ -d "$FALLBACK_SRC/biomes" ] && [ -d "$FALLBACK_SRC/environment" ]; then
            cp -r "$FALLBACK_SRC/biomes" "$SOUNDS_DIR/"
            cp -r "$FALLBACK_SRC/environment" "$SOUNDS_DIR/"
        else
            rm -rf /tmp/charmony-sounds-fallback
            echo "✗ Could not find biomes/ and environment/ in fallback repo"
            echo "  Expected: sounds/biomes + sounds/environment OR biomes/ + environment/ at repo root"
            exit 1
        fi
        
        rm -rf /tmp/charmony-sounds-fallback
        echo "✓ Copied sounds from fallback archive"
    else
        echo "✗ Could not clone fallback repo (check token permissions)"
        exit 1
    fi
fi

# Verify file count
SOUND_COUNT=$(find "$SOUNDS_DIR/biomes" "$SOUNDS_DIR/environment" -type f -name "*.ogg" 2>/dev/null | wc -l)
echo "Found $SOUND_COUNT .ogg files"

if [ "$SOUND_COUNT" -lt "$EXPECTED_MIN_FILES" ]; then
    echo "✗ Expected at least $EXPECTED_MIN_FILES sound files, found only $SOUND_COUNT"
    exit 1
fi

echo "✓ Sound assets fetched successfully ($SOUND_COUNT files)"
