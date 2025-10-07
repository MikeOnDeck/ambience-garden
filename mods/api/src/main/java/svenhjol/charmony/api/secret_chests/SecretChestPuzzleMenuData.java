package svenhjol.charmony.api.secret_chests;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import svenhjol.charmony.api.stone_chests.StoneChestMaterial;

/**
 * Data holder passed to the puzzle menu provider.
 * Populated by the stone chest block entity when the player first tries to open a chest.
 */
public class SecretChestPuzzleMenuData {
    public int syncId;
    public Inventory playerInventory;
    public ServerLevel level;
    public BlockPos pos;
    public StoneChestMaterial material;
    public RandomSource random;
    public long seed;
    public int difficultyAmplifier;
}
