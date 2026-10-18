# Charmony

A suite of mods for Minecraft. WIP.

All mod jars require the **charmony** jar to be present in order to function.
Collections, which are bundles of mods, include the charmony jar by default.

## Building mods

- Enable/disable mods in `settings.gradle`.
- Run `./gradlew build` to build everything.
- Run `./gradlew copyJars` to copy built jars to `./jars`.
- Note that disabling a mod may cause a collection to fail to build if it depends on that mod.

## Building collections

- Enable/disable collections in `settings.gradle`.
- Run `./gradlew build` to build everything.
- Run `./gradlew copyJars` to copy built jars to `./jars`. Note that this will also copy built mod jars.

## Building Charmonium (1.21.11)

This fork includes a streamlined build configuration for **Charmonium** on Minecraft 1.21.11 (Fabric).

### Local Build

**Requirements:**
- Java 21 (Temurin recommended)
- Git

**Steps:**

1. Fetch sound assets (required before building):
   ```bash
   ./scripts/fetch-sounds.sh
   ```
   This downloads ambient sound files from [svenhjol/charmony-ambient-sounds](https://github.com/svenhjol/charmony-ambient-sounds) (public) at the pinned commit. If that fails, it falls back to a private archive using the `SOUNDS_ARCHIVE_TOKEN` environment variable.

2. Build:
   ```bash
   ./gradlew build
   ```

3. Find the built jar:
   ```
   collections/charmonium/build/libs/charmonium-8.9.0+1.21.11.jar
   ```

**Note:** Sound assets (`.ogg` files) are **never** committed to this public repository. They are fetched at build time and are `.gitignore`d. The sound file licenses are unconfirmed, which is why they remain external.

### GitHub Actions Build

The repository includes a GitHub Actions workflow that builds Charmonium automatically:

- **Automatic builds:** Triggered on push to `1.21.11` and pull requests.
- **Manual builds:** Go to **Actions** → **Build Charmonium** → **Run workflow**. You can optionally check "Create GitHub Release" to publish a draft release with the jar attached.

**Setup:**

The public upstream sound repository should work without secrets. If it's unavailable, create a repository secret:

- **Name:** `SOUNDS_ARCHIVE_TOKEN`
- **Value:** A GitHub personal access token (fine-grained recommended) with `contents:read` permission for `MikeOnDeck/charmony-sounds-archive`.

To create a fine-grained token:
1. Go to GitHub Settings → Developer settings → Personal access tokens → Fine-grained tokens
2. Generate new token with Repository access: Only select repositories → `MikeOnDeck/charmony-sounds-archive`
3. Permissions: Repository permissions → Contents → Read-only
4. Add the token as a repository secret in Settings → Secrets and variables → Actions

### Credits

Original **Charmonium** and **Charmony** framework by [svenhjol](https://github.com/svenhjol).

**License:** MIT  
Copyright (c) 2022 Sven Hjól

Sound assets are sourced from [svenhjol/charmony-ambient-sounds](https://github.com/svenhjol/charmony-ambient-sounds). Their licensing is unconfirmed and they are not included in this repository.
