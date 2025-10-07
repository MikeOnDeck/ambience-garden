package svenhjol.charmony.core.common.features.wood.blocks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import svenhjol.charmony.api.core.IgniteProvider;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;

import java.util.function.Supplier;

public class CustomGateBlock extends FenceGateBlock implements IgniteProvider {
    protected final WoodMaterial material;

    public CustomGateBlock(ResourceKey<Block> key, WoodMaterial material) {
        super(material.woodType(), material.blockProperties()
            .strength(2.0f, 3.0f)
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

    public static class GateBlockItem extends BlockItem {
        public <T extends Block> GateBlockItem(ResourceKey<Item> key, Supplier<T> block) {
            super(block.get(), new Properties().setId(key));
        }
    }
}
