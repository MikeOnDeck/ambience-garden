package svenhjol.charmony.core.common.features.teleport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import svenhjol.charmony.core.Charmony;
import svenhjol.charmony.core.base.Log;
import svenhjol.charmony.core.helpers.TeleportHelper;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("unused")
public abstract class Teleporter {
    protected static final Log LOGGER = new Log(Charmony.ID, "Teleport");
    protected static final int MAX_TICKS = 200;

    private final boolean survivalMode;
    private final ServerPlayer player;
    private final ServerLevel level;
    private boolean hasDoneEffects = false;
    private boolean valid;
    private int ticks = 0;

    public Teleporter(ServerPlayer player) {
        this.player = player;
        this.level = (ServerLevel)player.level();
        this.valid = true;
        this.survivalMode = !player.getAbilities().instabuild && !player.isSpectator();
    }

    public void tick() {
        if (!isValid()) {
            return;
        }

        ticks++;

        if (ticks == playSoundAfterTicks() && isSameDimension()) {
            playTeleportSound();
        }

        if (ticks < teleportAfterTicks()) return;

        if (ticks == teleportAfterTicks()) {
            teleport();
            return;
        }

        if (ticks < repositionAfterTicks()) return;

        if (player.isRemoved() || ticks >= MAX_TICKS) {
            removePlayerSafety();
            valid = false;
            return;
        }

        reposition();
    }

    protected abstract ResourceKey<Level> getTargetDimension();

    protected abstract Vec3 getTargetPos();

    protected ServerLevel getLevel() {
        return level;
    }

    protected ServerPlayer getPlayer() {
        return player;
    }

    protected boolean isValid() {
        return valid;
    }

    protected boolean isSameDimension() {
        return getTargetDimension() == level.dimension();
    }

    protected boolean useExactPosition() {
        return false;
    }

    protected void applyProtection() {
        var protectionTicks = protectionDurationTicks();
        var effects = new ArrayList<>(Arrays.asList(
            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, protectionTicks, 1),
            new MobEffectInstance(MobEffects.RESISTANCE, protectionTicks, 1),
            new MobEffectInstance(MobEffects.REGENERATION, protectionTicks, 1)
        ));
        effects.forEach(player::addEffect);
    }

    protected int teleportAfterTicks() {
        return Teleport.feature().teleportAfterTicks();
    }

    protected int repositionAfterTicks() {
        return Teleport.feature().repositionAfterTicks();
    }

    protected int playSoundAfterTicks() {
        return Teleport.feature().playSoundAfterTicks();
    }

    protected int protectionDurationTicks() {
        return Teleport.feature().protectionDurationTicks();
    }

    protected void doEffects() {
        // If it was dimensional travel, play the sound now.
        if (!isSameDimension()) {
            playTeleportSound();
        }
    }

    protected void playTeleportSound() {
        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.BLOCKS);
    }

    protected BlockState getSurfaceBlockForDimension() {
        var level = player.level();
        BlockState state;

        if (level.dimension() == Level.END) {
            state = Blocks.END_STONE.defaultBlockState();
        } else if (level.dimension() == Level.NETHER) {
            state = Blocks.NETHERRACK.defaultBlockState();
        } else {
            state = Blocks.STONE.defaultBlockState();
        }

        return state;
    }

    private void teleport() {
        LOGGER.debug("Called teleport for player " + player);

        var dimension = getTargetDimension();
        var target = getTargetPos();

        // Add protection effects to the teleporting player.
        if (protectionDurationTicks() > 0) {
            applyProtection();
        }

        // If survival mode then temporarily make the player invulnerable and fix their position in space.
        applyPlayerSafety();

        // Do the teleport to the location - repositioning comes later.
        if (!isSameDimension()) {
            var server = level.getServer();
            var newDimension = server.getLevel(dimension);
            if (newDimension != null) {
                TeleportHelper.changeDimension(player, newDimension, target);
                valid = true;
            }
            return;
        }

        player.teleportTo(target.x, target.y, target.z);
        valid = true;
    }

    private void reposition() {
        var target = getTargetPos();
        var useExactPosition = useExactPosition();

        if (useExactPosition) {
            move(new BlockPos((int)target.x, (int)target.y, (int)target.z));
            return;
        }

        var level = player.level();
        var seaLevel = level.getSeaLevel();
        if (seaLevel <= 0) {
            seaLevel = 64; // This is dumb.
        }
        var pos = new BlockPos((int)target.x, seaLevel, (int)target.z);

        if (!level.dimensionType().hasCeiling()) {
            var surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
            LOGGER.debug("Found a valid surface position " + surface);
            var posBelow = surface.below();
            var stateBelow = level.getBlockState(posBelow);

            if (!(stateBelow.isAir() && !stateBelow.getFluidState().is(Fluids.LAVA))
                || stateBelow.getFluidState().is(Fluids.WATER)
                || stateBelow.getFluidState().is(Fluids.FLOWING_WATER)) {
                move(surface.above());
                return;
            } else {
                LOGGER.debug("Unable to place player on surface because state=" + stateBelow + ", falling back to checks");
            }
        } else {
            var surface = TeleportHelper.getSurfacePos(level, pos, Math.min(seaLevel + 40, level.getHeight() - 20));

            if (surface != null) {
                move(surface);
                return;
            } else {
                LOGGER.debug("Unable to place player in an air space, falling back to checks");
            }
        }

        var mutable = new BlockPos.MutableBlockPos();
        mutable.set(pos.getX(), seaLevel + 24, pos.getZ());

        var surfaceBlock = getSurfaceBlockForDimension();
        var validFloor = false;
        var validCurrent = false;
        var validAbove = false;

        // Check blocks below for solid ground or water
        for (int tries = 0; tries < 48; tries++) {
            var above = level.getBlockState(mutable.above());
            var current = level.getBlockState(mutable);
            var below = level.getBlockState(mutable.below());

            validFloor = below.isSolidRender() || level.isWaterAt(mutable.below());
            validCurrent = current.isAir() || level.isWaterAt(mutable);
            validAbove = above.isAir() || level.isWaterAt(mutable.above());

            if (validFloor && validCurrent && validAbove) {
                LOGGER.debug("Found valid calculated position "  + mutable + " after " + tries + " tries");
                move(mutable);
                return;
            }

            mutable.move(Direction.DOWN);
        }

        if (!validFloor) {
            makePlatform(mutable.below(), surfaceBlock);
        }
        if (!validCurrent) {
            level.setBlock(mutable, Blocks.AIR.defaultBlockState(), 2);
            LOGGER.debug("Made air space at " + mutable);

            // Check each cardinal point for lava
            for (var direction : Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
                var relative = mutable.relative(direction);
                var state = level.getBlockState(relative);

                if (state.is(Blocks.LAVA)) {
                    makeWall(relative, surfaceBlock);
                }
            }
        }
        if (!validAbove) {
            LOGGER.debug("Made air space at " + mutable);
            level.setBlock(mutable.above(), Blocks.AIR.defaultBlockState(), 2);
        }

        // Check that lava doesn't pour down into the new gap
        var relative = mutable.above(2);
        if (level.getBlockState(relative).is(Blocks.LAVA)) {
            makePlatform(relative, surfaceBlock);
        }

        move(mutable);
    }

    /**
     * Move the player into position after making the location safe.
     */
    private void move(BlockPos pos) {
        player.snapTo(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);
        player.teleportTo(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);

        if (!hasDoneEffects) {
            doEffects();
            hasDoneEffects = true;
        }

        removePlayerSafety();
        valid = false;
    }

    private void makeWall(BlockPos pos, BlockState state) {
        var level = player.level();
        level.setBlock(pos, state, 2);
        level.setBlock(pos.above(), state, 2);
        LOGGER.debug("Made wall at " + pos);
    }

    private void makePlatform(BlockPos pos, BlockState solid) {
        var level = player.level();
        var x = pos.getX();
        var y = pos.getY();
        var z = pos.getZ();

        BlockPos.betweenClosed(x - 1, y, z - 1, x + 1, y, z + 1).forEach(
            p -> level.setBlockAndUpdate(p, solid));
        LOGGER.debug("Made platform at " + pos);
    }

    private void applyPlayerSafety() {
        if (survivalMode) {
            player.setInvulnerable(true);
            player.setNoGravity(true);
            LOGGER.debug("Applied player safety");
        }
    }

    private void removePlayerSafety() {
        if (survivalMode) {
            player.setInvulnerable(false);
            player.setNoGravity(false);
            LOGGER.debug("Removed player safety");
        }
    }
}
