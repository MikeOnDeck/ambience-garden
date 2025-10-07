package svenhjol.charmony.core.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;

@SuppressWarnings("unused")
public final class WorldHelper {
    public static boolean isDay(Player player) {
        var level = player.level();
        var dayTime = level.getDayTime() % 24000;
        return dayTime >= 0 && dayTime < 12700;
    }

    public static boolean isNight(Player player) {
        var level = player.level();
        var dayTime = level.getDayTime() % 24000;
        return dayTime >= 12700;
    }

    public static boolean isThundering(Player player) {
        var level = player.level();
        return level.isThundering();
    }

    public static boolean isOutside(Player player) {
        var level = player.level();
        if (player.isUnderWater()) return false;

        var blocks = 24;
        var start = 1;

        var playerPos = player.blockPosition();

        if (level.canSeeSky(playerPos)) return true;
        if (level.canSeeSkyFromBelowWater(playerPos)) return true;

        for (int i = start; i < start + blocks; i++) {
            var check = new BlockPos(playerPos.getX(), playerPos.getY() + i, playerPos.getZ());
            var state = level.getBlockState(check);
            var block = state.getBlock();

            if (level.isEmptyBlock(check)) continue;

            // TODO: Tag for glass here
            if (state.is(Blocks.GLASS)
                || (block instanceof RotatedPillarBlock && state.is(BlockTags.LOGS))
                || block instanceof LeavesBlock
                || block instanceof HugeMushroomBlock
                || block instanceof StemBlock
            ) continue;

            if (level.canSeeSky(check)) return true;
            if (level.canSeeSkyFromBelowWater(check)) return true;
            if (state.canOcclude()) return false;
        }

        return level.canSeeSky(playerPos.above(blocks));
    }

    public static BlockPos addRandomOffset(Level level, BlockPos pos, RandomSource random, int min, int max) {
        var n = random.nextInt(max - min) + min;
        var e = random.nextInt(max - min) + min;
        var s = random.nextInt(max - min) + min;
        var w = random.nextInt(max - min) + min;

        pos = pos.north(random.nextBoolean() ? n : -n);
        pos = pos.east(random.nextBoolean() ? e : -e);
        pos = pos.south(random.nextBoolean() ? s : -s);
        pos = pos.west(random.nextBoolean() ? w : -w);

        // World border checking
        var border = level.getWorldBorder();
        var x = pos.getX();
        var y = pos.getY();
        var z = pos.getZ();

        if (x < border.getMinX()) {
            pos = new BlockPos((int)border.getMinX(), y, z);
        } else if (x > border.getMaxX()) {
            pos = new BlockPos((int)border.getMaxX(), y, z);
        }
        if (z < border.getMinZ()) {
            pos = new BlockPos(x, y, (int)border.getMinZ());
        } else if (z > border.getMaxZ()) {
            pos = new BlockPos(x, y, (int)border.getMaxZ());
        }

        return pos;
    }

    public static float distanceFromGround(Player player, int check) {
        var level = player.level();
        var pos = player.blockPosition();
        var playerHeight = pos.getY();

        // Sample points.
        var samples = List.of(
            pos.east(check),
            pos.west(check),
            pos.north(check),
            pos.south(check)
        );

        var avg = 0;
        for (BlockPos sample : samples) {
            avg += level.getHeight(Heightmap.Types.WORLD_SURFACE, sample.getX(), sample.getZ());
        }
        avg /= samples.size();
        return Math.max(0.0f, playerHeight - avg);
    }

    public static boolean isBelowSeaLevel(Player player) {
        var level = player.level();
        return player.blockPosition().getY() < level.getSeaLevel();
    }

    public static double getDistanceSquared(BlockPos pos1, BlockPos pos2) {
        double d0 = pos1.getX();
        double d1 = pos1.getZ();
        double d2 = d0 - pos2.getX();
        double d3 = d1 - pos2.getZ();
        return d2 * d2 + d3 * d3;
    }

    /**
     * Generates a unique seed from block position coordinates.
     *
     * @return A unique long seed value.
     */
    public static long seedFromBlockPos(BlockPos pos) {
        var x = pos.getX();
        var y = pos.getY();
        var z = pos.getZ();

        return ((long)x & 0x3FFFFFFL) << 38
            | ((long)(y + 64) & 0x3FFL) << 28
            | ((long)z & 0x3FFFFFFL);
    }

    /**
     * Get a random cardinal direction.
     *
     * @param random Random source to use.
     * @return A random cardinal direction.
     */
    public static Direction randomCardinal(RandomSource random) {
        var cardinals = List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
        return cardinals.get(random.nextInt(cardinals.size() - 1));
    }

    /**
     * Gets the nice name of the biome that the recipient is in.
     */
    public static String biomeName(Player player) {
        return Component.translatable(biomeLocaleKey(player)).getString();
    }

    /**
     * Get a locale key for the biome at the recipient's current position.
     */
    public static String biomeLocaleKey(Player player) {
        var registry = player.level().registryAccess();
        var biome = player.level().getBiome(player.blockPosition());
        var key = registry.lookupOrThrow(Registries.BIOME).getKey(biome.value());

        if (key == null) {
            throw new RuntimeException("Cannot get recipient biome");
        }

        var namespace = key.getNamespace();
        var path = key.getPath();
        return "biome." + namespace + "." + path;
    }
}
