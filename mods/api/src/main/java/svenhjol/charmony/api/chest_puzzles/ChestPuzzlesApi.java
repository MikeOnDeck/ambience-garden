package svenhjol.charmony.api.chest_puzzles;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import svenhjol.charmony.api.stone_chests.StoneChestBlockEntity;
import svenhjol.charmony.api.stone_chests.StoneChestMaterial;

import java.util.Optional;

@SuppressWarnings("unused")
public final class ChestPuzzlesApi {
    private static ChestPuzzlesApi instance;
    private MenuProviderCreator creatorImpl;

    public static ChestPuzzlesApi instance() {
        if (instance == null) {
            instance = new ChestPuzzlesApi();
        }
        return instance;
    }

    private ChestPuzzlesApi() {
        this.creatorImpl = ((level, chest, syncId, inventory, material) -> Optional.empty());
    }

    public Optional<AbstractContainerMenu> getMenuProvider(
        ServerLevel level,
        StoneChestBlockEntity chest,
        int syncId,
        Inventory inventory,
        StoneChestMaterial material
    ) {
        return creatorImpl.create(level, chest, syncId, inventory, material);
    }

    @FunctionalInterface
    public interface MenuProviderCreator {
        Optional<AbstractContainerMenu> create(
            ServerLevel level,
            StoneChestBlockEntity chest,
            int syncId,
            Inventory inventory,
            StoneChestMaterial material
        );
    }

    public static class Impl {
        public static void menuProviderCreator(MenuProviderCreator creator) {
            ChestPuzzlesApi.instance().creatorImpl = creator;
        }
    }
}
