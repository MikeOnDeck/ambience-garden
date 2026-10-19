# Ambience Garden

**Ambient biome sounds that make your world feel alive.**

Ambience Garden adds immersive ambient sounds to Minecraft's biomes and environments. Standalone client-side mod for Minecraft 1.21.11 (Fabric), compatible with vanilla servers including Realms.

## Credits

Ambience Garden is a rebrand and standalone version of **Charmonium** by [svenhjol](https://github.com/svenhjol).

- **Original Charmony framework and Charmonium mod**: Copyright (c) 2022 Sven Hjól
- **Ambience Garden standalone rebrand**: Copyright (c) 2026 MikeOnDeck

### Source Material

This mod is derived from:
- [svenhjol/charmony](https://github.com/svenhjol/charmony) - Core framework and features
- [svenhjol/charmony-ambient-sounds](https://github.com/svenhjol/charmony-ambient-sounds) - Sound assets

## Building from Source

### Requirements

- Java 21 (Temurin recommended)
- Git

### Build Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/MikeOnDeck/ambience-garden.git
   cd ambience-garden
   ```

2. **Fetch sound assets** (required before building):
   ```bash
   ./scripts/fetch-sounds.sh
   ```
   
   This downloads ambient sound files from [svenhjol/charmony-ambient-sounds](https://github.com/svenhjol/charmony-ambient-sounds) at the pinned commit `02d2e42`. If that source is unavailable, it falls back to a private archive using the `SOUNDS_ARCHIVE_TOKEN` environment variable.

3. **Build the mod**:
   ```bash
   ./gradlew build
   ```

4. **Find the built jar**:
   ```
   build/libs/ambience-garden-1.0.0+1.21.11.jar
   ```

### Sound Assets and Licensing

**Important**: Sound assets (`.ogg` files) are **NOT included** in this repository. They are:
- Fetched at build time from upstream sources
- `.gitignore`d and never committed to version control
- CI checks fail if any `.ogg` files are tracked in git

**Sound asset licensing is currently under review.** Until licensing is clarified, **no public jar releases** are being made. This repository is for building personal/private copies only.

## Development Notes

### What Changed from Charmonium?

Ambience Garden is a **standalone rebrand** of Charmonium that:

1. **Merged dependencies**: The Charmony core, API, and ambient sounds modules are now integrated directly into one mod. No more nested jar-in-jar dependencies.

2. **New identity**:
   - Mod ID: `ambience_garden`
   - Package: `com.mikeondeck.ambiencegarden`
   - Asset namespace: `ambience_garden` (previously `charmony`)
   - Config file: `.minecraft/config/ambience_garden/ambience_garden-client.toml`

3. **Simplified scope**: Only ambient sounds functionality is included. Removed unused Charmony features (e.g., anvil behavior toggles).

4. **Asset migration**: Sound files moved from `assets/charmony/sounds/` to `assets/ambience_garden/sounds/`, and `sounds.json` keys updated accordingly.

5. **Build system**: Simplified from multi-module Gradle to single-module project. CI still fetches sounds at build time and prevents accidental commits of audio files.

### Dropped Features

The following Charmony core features were removed as they're not needed for ambient sounds:
- Anvil behavior customization
- Other library features unrelated to sound playback

## Contributing

Contributions are welcome! Please open an issue or pull request.

- **Bug reports**: Use the [bug report template](.github/ISSUE_TEMPLATE/bug_report.md)
- **Sound suggestions**: Use the [sound/biome suggestion template](.github/ISSUE_TEMPLATE/sound_suggestion.md)
- **Pull requests**: See [CONTRIBUTING.md](CONTRIBUTING.md)

All contributions are reviewed by MikeOnDeck.

## License

MIT License - See [LICENSE](LICENSE) for full text.

## Links

- **GitHub**: https://github.com/MikeOnDeck/ambience-garden
- **Original Charmony**: https://github.com/svenhjol/charmony
- **Original Charmonium**: https://github.com/svenhjol/charmony (Charmonium collection)
