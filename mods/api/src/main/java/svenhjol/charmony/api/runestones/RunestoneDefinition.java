package svenhjol.charmony.api.runestones;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface RunestoneDefinition {
    RunestoneType type();

    Supplier<? extends Block> baseBlock();

    Optional<RunestoneLocation> location(LevelAccessor level, BlockPos pos, RandomSource random, double quality);

    Supplier<ItemLike> item(LevelAccessor level, BlockPos pos, RandomSource random, double quality);
}