package svenhjol.charmony.core.common.features.wood.blocks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;

import java.util.function.Supplier;

public class CustomSaplingBlock extends SaplingBlock {
    protected final WoodMaterial material;

    public CustomSaplingBlock(ResourceKey<Block> key, WoodMaterial material, TreeGrower treeGrower) {
        super(treeGrower, Properties.of()
            .noCollision()
            .randomTicks()
            .instabreak()
            .sound(SoundType.GRASS)
            .setId(key));

        this.material = material;
    }

    public static class SaplingBlockItem extends BlockItem {
        public <T extends Block> SaplingBlockItem(ResourceKey<Item> key, Supplier<T> block) {
            super(block.get(), new Properties().setId(key));
        }
    }
}
