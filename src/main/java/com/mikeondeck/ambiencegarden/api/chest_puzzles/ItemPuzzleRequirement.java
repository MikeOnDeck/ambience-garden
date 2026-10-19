package com.mikeondeck.ambiencegarden.api.chest_puzzles;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public record ItemPuzzleRequirement(
    ResourceKey<LootTable> lootTable,
    int slots
) {
}
