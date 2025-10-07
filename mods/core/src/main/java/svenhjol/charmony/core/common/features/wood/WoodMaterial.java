package svenhjol.charmony.core.common.features.wood;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import svenhjol.charmony.api.core.Material;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public interface WoodMaterial extends Material {
    @Override
    default NoteBlockInstrument noteBlockInstrument() {
        return NoteBlockInstrument.BASS; // Vanilla default for wood
    }

    @Override
    default boolean isFlammable() {
        return true; // Vanilla default for wood
    }

    BlockSetType blockSetType();

    default float leafParticleChance() {
        return 0.01f; // Vanilla default
    }

    default Map<CustomWood, Supplier<ItemLike>> creativeMenuPosition() {
        Map<CustomWood, Supplier<ItemLike>> map = new LinkedHashMap<>();
        map.put(CustomWood.BARREL, () -> Items.BARREL);
        map.put(CustomWood.CHEST_BOAT, () -> Items.ACACIA_CHEST_BOAT);
        map.put(CustomWood.BOAT, () -> Items.ACACIA_CHEST_BOAT);
        map.put(CustomWood.BOOKSHELF, () -> Items.BOOKSHELF);
        map.put(CustomWood.BUTTON, () -> Items.ACACIA_BUTTON);
        map.put(CustomWood.CHEST, () -> Items.CHEST);
        map.put(CustomWood.CHISELED_BOOKSHELF,   () -> Items.CHISELED_BOOKSHELF);
        map.put(CustomWood.DOOR, () -> Items.ACACIA_BUTTON);
        map.put(CustomWood.FENCE, () -> Items.ACACIA_BUTTON);
        map.put(CustomWood.GATE, () -> Items.ACACIA_BUTTON);
        map.put(CustomWood.HANGING_SIGN, () -> Items.ACACIA_HANGING_SIGN);
        map.put(CustomWood.LADDER, () -> Items.LADDER);
        map.put(CustomWood.LEAVES, () -> Items.ACACIA_LEAVES);
        map.put(CustomWood.LOG, () -> Items.ACACIA_BUTTON);
        map.put(CustomWood.PLANKS, () -> Items.ACACIA_BUTTON);
        map.put(CustomWood.PRESSURE_PLATE, () -> Items.ACACIA_BUTTON);
        map.put(CustomWood.SAPLING, () -> Items.ACACIA_SAPLING);
        map.put(CustomWood.SIGN, () -> Items.ACACIA_HANGING_SIGN);
        map.put(CustomWood.SLAB, () -> Items.ACACIA_BUTTON);
        map.put(CustomWood.STAIRS, () -> Items.ACACIA_BUTTON);
        map.put(CustomWood.STRIPPED_LOG, () -> Items.ACACIA_BUTTON);
        map.put(CustomWood.STRIPPED_WOOD, () -> Items.ACACIA_BUTTON);
        map.put(CustomWood.TRAPDOOR, () -> Items.ACACIA_BUTTON);
        map.put(CustomWood.TRAPPED_CHEST, () -> Items.TRAPPED_CHEST);
        map.put(CustomWood.WOOD, () -> Items.ACACIA_BUTTON);
        return map;
    }

    /**
     * Define TreeGrower configuration for a custom sapling.
     *
     * @see TreeGrower
     */
    default Optional<TreeGrower> tree() {
        return Optional.empty();
    }

    WoodType woodType();
}
