# Changelog

## 1.27.0

- Bump for 1.21.9 snapshot.

## 1.26.15

- Change schema for Stone Chest block entity.

## 1.26.13

- Weaken requirement for StoneChests API block and block entity.

## 1.26.10

- Update Elixirs API.

## 1.26.9

- Make override nullable.

## 1.26.7

- Add ChestPuzzles API.

## 1.26.6

- Update StoneChestBlockEntity methods.

## 1.26.4

- Update other APIs to use new way of defining implementation methods.

## 1.26.3

- Add StoneChests API.

## 1.25.0

- Add Elixirs API.

## 1.24.2

- Default relic glint colors now choose from any color other than purple.
- Suppress some warnings about unused methods.
- Fix icon location.

## 1.24.1

- Rename "sacrifice" to "item".

## 1.24.0

- `getColor()` is now `getIntColor()`.
- Add `getArgbColor()`.

## 1.23.5

- Move TintedGuiGraphics back into core.

## 1.23.4

- Add custom HudDisplayCallback.

## 1.23.3

- Restore tinted changes.

## 1.23.2

- Rename "TintedGuiGraphics" to "Tinted" as it no longer targets the original class.

## 1.23.0

- Merge from core.

## 1.22.0

- Merge from separate mods.

## 1.21.0

- Update runestones API to use a runestone type reference.

## 1.20.1

- Update Glint Colors API.

## 1.20.0

- Move interfaces, enums and helper classes into their own packages.

## 1.19.2

- Added tuff as a stone chest material.

## 1.19.1

- Added calcite as a stone chest material.

## 1.19.0

- Change enum case. Prep for moving stone/secret chest APIs.

## 1.18.0

- Move event interfaces from Charmony.
- Output the number of failure attempts when making an API request.
- Deprecate the RunicWords, this will be reworked in a future update.

## 1.17.0

- Removed the deprecated getOffsetXY() method.

## 1.16.0

- Rename the "dark mode" API to make it consistent.
- Expand the WandererTradeProvider.

## 1.15.3

- Add UsesDarkMode.

## 1.15.2

- Allow responding to recipe buttons.

## 1.15.1

- Allow defaults in tweaks.

## 1.15.0

- Add container and player XY offsets to tweaks.

## 1.14.2

- Add some more nether debris replacements.

## 1.14.1

- Add chestLootTables and barrelLootTables to use when placing a chest or barrel block.
- Chance for dirt debris to contain barrels or chests.

## 1.14.0

- Change signatures of stone circle definition addAtCenter and debrisReplacements.

## 1.13.0

- Rename plurals for consistency: StoneCircleDefinitionProvider, RunestoneDefinitionProvider

## 1.12.6

- Update default stone circle definitions to remove warm ocean loot table, dilute grass block replacement and make sandstone replaceable.

## 1.12.5

- Update default stone circle definitions floor replacements, loot tables and debris range.

## 1.12.4

- Incorrect method plural name.

## 1.12.3

- Add new functionality to Stone Circle definitions:
    - Chance of debris being placed around each pillar
    - Blocks that debris can turn into
    - Loot tables to use if the debris is a suspicious block

## 1.12.2

- Pass finalized registry access to the getRuneWords() method.

## 1.12.1

- Add RuneWordProvider for mods to register custom objects as rune words.

## 1.11.0

- Update for Minecraft 1.21.5.

## 1.8.4

- Add an isValid() method to the runic power API.

## 1.8.3

- Add a consume() method to the runic power API.
- Add API documentation.

## 1.8.2

- Add RunicPower and provider.

## 1.8.1

- GlintColorApi singleton.

## 1.8.0

- Add GlintColorApi.
- Fix invalid class path.

## 1.7.3

- Update stone circle definition with decay and max runestones options.

## 1.7.2

- Simplify runestone rarity/quality. 1.7.2

## 1.7.1

- Add runestone rarity to stone circle definition API.

## 1.7.0

- Change case of enums.
- Add rarity to runestone location API.
- Add more features to stone circle definition API.

## 1.6.4

- Add Runestone and Stone Circle definitions.

## 1.6.3

- Add WandererTradeProvider.

## 1.6.2

- Add TotemInventoryCheckProvider and TotemPreservingProvider.

## 1.6.1

- Update icon.

## 1.6.0

- Build for 1.21.4.

## 1.5.2

- Fix interface of AutoPickupItemProvider to return a function.

## 1.5.1

- Add QuickReplantProvider and AutoPickupItemProvider.

## 1.5.0

- Add GrindableItemProvider.

## 1.4.0

- Build for 1.21.3

## 1.1.3-4

- Update to loom 1.8 and Minecraft 1.21.2-pre3

## 1.1.2

- Variable pack version.

## 1.1.1

- Build for 1.21.2-pre2

## 1.1.0

- Build for 1.21.2-pre1

## 1.0.4

- Add ItemTidying API.

## 1.0.3

- Bump pack version for latest snapshot.

## 1.0.2

- Add icon.

## 1.0.1

- Bump to 24w40a.

## 1.0.0

- Add CompassOverlayItem and provider