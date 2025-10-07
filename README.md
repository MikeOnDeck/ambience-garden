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
