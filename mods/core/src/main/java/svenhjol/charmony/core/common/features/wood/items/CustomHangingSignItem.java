package svenhjol.charmony.core.common.features.wood.items;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;

import java.util.Map;
import java.util.function.Supplier;

public class CustomHangingSignItem extends HangingSignItem {
    protected final WoodMaterial material;
    protected final Supplier<? extends CeilingHangingSignBlock> hangingBlock;
    protected final Supplier<? extends WallHangingSignBlock> wallSignBlock;

    public <S extends CeilingHangingSignBlock, W extends WallHangingSignBlock> CustomHangingSignItem(ResourceKey<Item> key, WoodMaterial material, Supplier<S> hangingBlock, Supplier<W> wallSignBlock) {
        super(hangingBlock.get(), wallSignBlock.get(), new Properties()
            .stacksTo(16)
            .setId(key));

        this.material = material;
        this.hangingBlock = hangingBlock;
        this.wallSignBlock = wallSignBlock;
    }

    @Override
    public void registerBlocks(Map<Block, Item> map, Item item) {
        super.registerBlocks(map, item);
        map.put(this.wallSignBlock.get(), item);
    }

    public Supplier<? extends CeilingHangingSignBlock> getHangingBlock() {
        return hangingBlock;
    }

    public Supplier<? extends WallHangingSignBlock> getWallSignBlock() {
        return wallSignBlock;
    }
}
