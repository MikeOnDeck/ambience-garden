package svenhjol.charmony.api.stone_chests;

import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class StoneChestsApi {
    private static StoneChestsApi instance;

    private Function<StoneChestMaterial, Optional<Block>> getBlockImpl;

    public static StoneChestsApi instance() {
        if (instance == null) {
            instance = new StoneChestsApi();
        }
        return instance;
    }

    private StoneChestsApi() {
        getBlockImpl = material -> Optional.empty();
    }

    public Optional<Block> getBlock(StoneChestMaterial material) {
        return getBlockImpl.apply(material);
    }

    public static class Impl {
        public static void getBlock(Function<StoneChestMaterial, Optional<Block>> impl) {
            instance().getBlockImpl = impl;
        }
    }
}
