package svenhjol.charmony.core.common.features.wood.blocks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;

import java.util.function.Supplier;

public class CustomWoodenPressurePlateBlock extends PressurePlateBlock {
    protected final WoodMaterial material;

    public CustomWoodenPressurePlateBlock(ResourceKey<Block> key, WoodMaterial material) {
        super(material.blockSetType(), material.blockProperties()
            .strength(0.5f)
            .noCollision()
            .setId(key));

        this.material = material;
    }

    public static class WoodenPressurePlateBlockItem extends BlockItem {
        public <T extends Block> WoodenPressurePlateBlockItem(ResourceKey<Item> key, Supplier<T> block) {
            super(block.get(), new Properties().setId(key));
        }
    }
}
