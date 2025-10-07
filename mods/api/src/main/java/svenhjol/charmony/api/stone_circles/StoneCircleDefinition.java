package svenhjol.charmony.api.stone_circles;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings({"NullableProblems", "unused"})
public interface StoneCircleDefinition extends StringRepresentable {
    String name();

    TagKey<Block> pillarBlocks();

    Optional<Supplier<? extends Block>> runestoneBlock();

    default Pair<Integer, Integer> pillarHeight() {
        return Pair.of(4, 8);
    }

    default Pair<Integer, Integer> pillarThickness() { return Pair.of(1, 1); }

    default Pair<Integer, Integer> radius() {
        return Pair.of(6, 15);
    }

    default Pair<Integer, Integer> degrees() {
        return Pair.of(45, 45);
    }

    default int circleJitter() {
        return 0;
    }

    default int maxRunestonesPerPillar() {
        return 2;
    }

    default int maxRunestonesPerCircle() {
        return 8;
    }

    default double runestoneChance() {
        return 0.72d;
    }

    default double runestoneQuality() {
        return 0d;
    }

    /**
     * Chance of a pillar having a block replaced with air.
     *
     * @return Chance of pillar block replacement with air.
     */
    default double decayChance() {
        return 0.4d;
    }

    /**
     * The maximum tolerated height difference from the starting Y position of the pillars.
     * If the terrain is higher than this value the pillar will not be generated.
     * This should be set quite high if the terrain is noisy, for example in the Nether or a rugged overworld biome.
     *
     * @return Maximum tolerated height difference.
     */
    default int terrainHeightTolerance() {
        return 20;
    }

    /**
     * Chance that debris (such as gravel and suspicious blocks) will be placed around and below each pillar.
     *
     * @return Chance of debris.
     */
    default double debrisChance() {
        return 0.4d;
    }

    /**
     * Number of times to try placing a debris block.
     *
     * @return Number of times to try placing a debris block.
     */
    default int debrisRolls() {
        return 10;
    }

    /**
     * How many blocks away from the pillar a debris block should be placed.
     * This affects the X and Z coordinate placement. The Y is half the terrain height tolerance.
     *
     * @return Range of debris block placement, in blocks.
     */
    default int debrisRange() {
        return 4;
    }

    default BlockPos ceilingReposition(WorldGenLevel level, BlockPos pos) {
        return new BlockPos(pos.getX(), level.getMinY() + 15, pos.getZ());
    }

    /**
     * Called when generating a stone circle to try and place something in the center.
     *
     * @param level Level reference.
     * @param pos Block pos at the center of the circle.
     */
    default void addAtCenter(WorldGenLevel level, BlockPos pos) {}

    /**
     * The loot tables to use if a suspicious block is placed.
     *
     * @return List of loot table resource keys.
     */
    default List<ResourceKey<LootTable>> archaeologyLootTables() {
        return List.of(
            BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON,
            BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY,
            BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY,
            BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY
        );
    }

    /**
     * The loot tables to use if a chest block is placed.
     *
     * @return List of loot table resource keys.
     */
    default List<ResourceKey<LootTable>> chestLootTables() {
        return List.of(
            BuiltInLootTables.SIMPLE_DUNGEON
        );
    }

    /**
     * The loot tables to use if a barrel block is placed.
     *
     * @return List of loot table resource keys.
     */
    default List<ResourceKey<LootTable>> barrelLootTables() {
        return List.of(
            BuiltInLootTables.SIMPLE_DUNGEON
        );
    }

    /**
     * Default map of floor blocks to be replaced with another block from a corresponding list.
     * If a suspicious block is placed, a loot table from the archaeologyLootTable() method will be used.
     *
     * @return Map of floor blocks to their potential replacements.
     */
    default Map<Block, List<Pair<Block, Double>>> debrisReplacements() {
        Map<Block, List<Pair<Block, Double>>> replacements = new HashMap<>();

        replacements.put(Blocks.GRASS_BLOCK,
            new ArrayList<>(List.of(Pair.of(Blocks.DIRT, 0.5d), Pair.of(Blocks.GRAVEL, 0.4d), Pair.of(Blocks.SUSPICIOUS_GRAVEL, 0.3d))));
        replacements.put(Blocks.DIRT,
            new ArrayList<>(List.of(Pair.of(Blocks.GRAVEL, 0.4d), Pair.of(Blocks.SUSPICIOUS_GRAVEL, 0.55d), Pair.of(Blocks.COARSE_DIRT, 0.5d), Pair.of(Blocks.ROOTED_DIRT, 0.4d), Pair.of(Blocks.PODZOL, 0.1d), Pair.of(Blocks.CHEST, 0.1d), Pair.of(Blocks.BARREL, 0.1d))));
        replacements.put(Blocks.SAND,
            new ArrayList<>(List.of(Pair.of(Blocks.SANDSTONE, 0.5d), Pair.of(Blocks.SUSPICIOUS_SAND, 0.55d))));
        replacements.put(Blocks.SANDSTONE,
            new ArrayList<>(List.of(Pair.of(Blocks.SUSPICIOUS_SAND, 0.5d))));
        replacements.put(Blocks.RED_SAND,
            new ArrayList<>(List.of(Pair.of(Blocks.RED_SANDSTONE, 0.5d), Pair.of(Blocks.GRAVEL, 0.4d), Pair.of(Blocks.SUSPICIOUS_GRAVEL, 0.4d))));
        replacements.put(Blocks.GRAVEL,
            new ArrayList<>(List.of(Pair.of(Blocks.SUSPICIOUS_GRAVEL, 0.5d))));
        replacements.put(Blocks.STONE,
            new ArrayList<>(List.of(Pair.of(Blocks.COBBLESTONE, 0.5d), Pair.of(Blocks.GRAVEL, 0.5d), Pair.of(Blocks.SUSPICIOUS_GRAVEL, 0.5d))));
        replacements.put(Blocks.NETHERRACK,
            new ArrayList<>(List.of(Pair.of(Blocks.MAGMA_BLOCK, 0.5d), Pair.of(Blocks.SOUL_SAND, 0.5d), Pair.of(Blocks.NETHER_GOLD_ORE, 0.3d), Pair.of(Blocks.ANCIENT_DEBRIS, 0.1d))));
        replacements.put(Blocks.SOUL_SAND,
            new ArrayList<>(List.of(Pair.of(Blocks.BLACKSTONE, 0.5d), Pair.of(Blocks.SOUL_SOIL, 0.5d), Pair.of(Blocks.ANCIENT_DEBRIS, 0.15d))));
        replacements.put(Blocks.END_STONE,
            new ArrayList<>(List.of(Pair.of(Blocks.END_STONE_BRICKS, 0.5d))));

        return replacements;
    }

    @Override
    default String getSerializedName() {
        return name();
    }
}
