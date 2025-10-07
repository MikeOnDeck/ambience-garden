package svenhjol.charmony.core.common.features.wood.blocks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;

import java.util.function.Supplier;

public class CustomWoodenButtonBlock extends ButtonBlock {
    public CustomWoodenButtonBlock(ResourceKey<Block> key, WoodMaterial material) {
        super(material.blockSetType(), 30, material.blockProperties()
            .strength(0.5f)
            .setId(key));
    }

    public static class WoodenButtonBlockItem extends BlockItem {
        public WoodenButtonBlockItem(ResourceKey<Item> key, Supplier<CustomWoodenButtonBlock> block) {
            super(block.get(), new Properties().setId(key));
        }
    }
}
