package svenhjol.charmony.core.common.features.wood.blocks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;

import java.util.function.Supplier;

public class CustomWoodenDoorBlock extends DoorBlock {
    public CustomWoodenDoorBlock(ResourceKey<Block> key, WoodMaterial material) {
        super(material.blockSetType(), material.blockProperties()
            .noOcclusion()
            .strength(3.0f)
            .setId(key));
    }

    public static class WoodenDoorBlockItem extends BlockItem {
        public WoodenDoorBlockItem(ResourceKey<Item> key, Supplier<CustomWoodenDoorBlock> block) {
            super(block.get(), new Properties()
                .setId(key));
        }
    }
}
