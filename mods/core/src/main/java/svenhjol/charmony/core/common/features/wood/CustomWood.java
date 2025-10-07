package svenhjol.charmony.core.common.features.wood;

import java.util.List;

public enum CustomWood {
    BARREL,
    BOAT,
    BOOKSHELF,
    BUTTON,
    CHEST,
    CHEST_BOAT,
    CHISELED_BOOKSHELF,
    DOOR,
    FENCE,
    GATE,
    HANGING_SIGN,
    LADDER,
    LEAVES,
    LOG,
    PLANKS,
    PRESSURE_PLATE,
    SAPLING,
    SIGN,
    SLAB,
    STAIRS,
    STRIPPED_LOG,
    STRIPPED_WOOD,
    TRAPDOOR,
    TRAPPED_CHEST,
    WOOD;

    public static final List<CustomWood> BUILDING_BLOCKS = List.of(
        DOOR, FENCE, GATE, LOG, PLANKS, PRESSURE_PLATE, SLAB,
        STAIRS, STRIPPED_LOG, STRIPPED_WOOD, TRAPDOOR, WOOD
    );

    public static final List<CustomWood> FUNCTIONAL_BLOCKS = List.of(
        BARREL, BOOKSHELF, BUTTON, CHEST, CHISELED_BOOKSHELF,
        HANGING_SIGN, LADDER, SIGN, TRAPPED_CHEST
    );

    public static final List<CustomWood> NATURAL_BLOCKS = List.of(
        LOG, STRIPPED_LOG, LEAVES, SAPLING
    );

    public static final List<CustomWood> TOOLS_AND_UTILITIES = List.of(
        CHEST_BOAT, BOAT
    );
}
