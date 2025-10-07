package svenhjol.charmony.core.common.features.wood.blocks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import svenhjol.charmony.api.core.IgniteProvider;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;

import java.util.function.Supplier;

public class CustomLogBlock extends RotatedPillarBlock implements IgniteProvider {
    protected final WoodMaterial material;

    public CustomLogBlock(ResourceKey<Block> key, WoodMaterial material) {
        super(material.blockProperties()
            .strength(2.0f)
            .setId(key));

        this.material = material;
    }

    @Override
    public int igniteChance() {
        return material.isFlammable() ? 5 : 0;
    }

    @Override
    public int burnChance() {
        return material.isFlammable() ? 5 : 0;
    }

    public static class LogBlockItem extends BlockItem {
        public <T extends Block> LogBlockItem(ResourceKey<Item> key, Supplier<T> block) {
            super(block.get(), new Properties().setId(key));
        }
    }
}
