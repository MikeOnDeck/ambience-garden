package svenhjol.charmony.core.common.features.wood.blocks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import svenhjol.charmony.api.core.IgniteProvider;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;

import java.util.function.Supplier;

public class CustomLeavesBlock extends TintedParticleLeavesBlock implements IgniteProvider {
    protected final WoodMaterial material;

    public CustomLeavesBlock(ResourceKey<Block> key, float leafParticleChance, WoodMaterial material) {
        super(leafParticleChance, Properties.of()
            .strength(0.2f)
            .randomTicks()
            .sound(SoundType.GRASS)
            .noOcclusion()
            .isValidSpawn((state, world, pos, type) -> false)
            .isSuffocating((state, world, pos) -> false)
            .isViewBlocking((state, world, pos) -> false)
            .setId(key));

        this.material = material;
    }

    @Override
    public int igniteChance() {
        return material.isFlammable() ? 30 : 0;
    }

    @Override
    public int burnChance() {
        return material.isFlammable() ? 60 : 0;
    }

    public static class LeavesBlockItem extends BlockItem {
        public <T extends Block> LeavesBlockItem(ResourceKey<Item> key, Supplier<T> block) {
            super(block.get(), new Properties().setId(key));
        }
    }
}
