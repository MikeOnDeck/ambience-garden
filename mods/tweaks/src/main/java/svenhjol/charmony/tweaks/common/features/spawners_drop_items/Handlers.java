package svenhjol.charmony.tweaks.common.features.spawners_drop_items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import svenhjol.charmony.core.base.Setup;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Handlers extends Setup<SpawnersDropItems> {
    private static final int ITEMS_PER_STACK = 64; // This might change in a future minecraft version.

    public Handlers(SpawnersDropItems feature) {
        super(feature);
    }

    public void blockBreak(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        if (level.isClientSide()) return;
        if (feature().onlyPeaceful() && level.getDifficulty() != Difficulty.PEACEFUL) return;

        if (state.getBlock() == Blocks.SPAWNER && blockEntity instanceof SpawnerBlockEntity spawner) {
            var baseSpawner = spawner.getSpawner();
            var entity = baseSpawner.getOrCreateDisplayEntity(level, pos);
            if (entity == null) return;

            var type = entity.getType();
            for (var dropType : feature().registers.dropTypes.rowKeySet()) {
                if (!type.is(dropType)) continue;

                feature().registers.dropTypes.row(dropType).forEach((item, amount) -> {
                    var stacks = getItemStacks(item, amount);
                    for (var stack : stacks) {
                        for (var i = 0; i < amount; i++) {
                            var singleStack = new ItemStack(stack.getItem());
                            Vec3 vec3 = Vec3.atLowerCornerWithOffset(pos, 0.5d, 0.5d, 0.5d).offsetRandom(level.random, 0.7f);
                            var itemEntity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), singleStack);
                            level.addFreshEntity(itemEntity);
                        }
                    }
                });
            }
        }
    }

    private List<ItemStack> getItemStacks(Item item, int amount) {
        List<ItemStack> stacks = new ArrayList<>();
        int numberOfStacks = 1;
        int remaining = amount;

        if (amount > ITEMS_PER_STACK) {
            numberOfStacks = amount / ITEMS_PER_STACK;
        }

        for (int i = 0; i < numberOfStacks; i++) {
            var stack = new ItemStack(item);
            stack.setCount(Math.min(remaining, ITEMS_PER_STACK));
            stacks.add(stack);
            remaining -= ITEMS_PER_STACK;
        }

        return stacks;
    }
}
