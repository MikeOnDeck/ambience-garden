package svenhjol.charmony.api.secret_chests;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public final class SecretChestsApi {
    private static SecretChestsApi instance;

    private ChestCreator creatorImpl;

    public static SecretChestsApi instance() {
        if (instance == null) {
            instance = new SecretChestsApi();
        }
        return instance;
    }

    private SecretChestsApi() {
        this.creatorImpl = (definition, level, random, pos, waterlogged, facing) -> false;
    }

    public boolean createChest(
        SecretChestDefinition definition,
        ServerLevelAccessor level,
        RandomSource random,
        BlockPos pos,
        boolean waterlogged,
        @Nullable Direction facing
    ) {
        return creatorImpl.create(definition, level, random, pos, waterlogged, facing);
    }

    @FunctionalInterface
    public interface ChestCreator {
        boolean create(
            SecretChestDefinition definition,
            ServerLevelAccessor level,
            RandomSource random,
            BlockPos pos,
            boolean waterlogged,
            @Nullable Direction facing
        );
    }

    public static class Impl {
        public static void chestCreator(ChestCreator creator) {
            SecretChestsApi.instance().creatorImpl = creator;
        }
    }
}
