package svenhjol.charmony.core.common.features.wood.blocks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import svenhjol.charmony.api.core.IgniteProvider;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;

import java.util.function.Supplier;

public class CustomWoodenTrapdoorBlock extends TrapDoorBlock implements IgniteProvider {
    protected final WoodMaterial material;

    public CustomWoodenTrapdoorBlock(ResourceKey<Block> key, WoodMaterial material) {
        super(material.blockSetType(), material.blockProperties()
            .noOcclusion()
            .strength(3.0f)
            .isValidSpawn((state, world, pos, type) -> false)
            .setId(key));

        this.material = material;
    }

    @Override
    public int igniteChance() {
        return material.isFlammable() ? 5 : 0;
    }

    @Override
    public int burnChance() {
        return material.isFlammable() ? 20 : 0;
    }

    public static class WoodenTrapdoorBlockItem extends BlockItem {
        public <T extends Block> WoodenTrapdoorBlockItem(ResourceKey<Item> key, Supplier<T> block) {
            super(block.get(), new Properties().setId(key));
        }
    }
}
