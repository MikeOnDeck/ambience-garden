package svenhjol.charmony.tweaks.common.features.pigs_find_mushrooms;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import svenhjol.charmony.core.Charmony;

public final class Tags {
    public static final TagKey<Block> PIGS_FIND_MUSHROOMS = TagKey.create(Registries.BLOCK,
        Charmony.id("pigs_find_mushrooms"));
}
