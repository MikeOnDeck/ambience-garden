package svenhjol.charmony.ambient_sounds.client.features.environment.sounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import svenhjol.charmony.ambient_sounds.client.features.environment.EnvironmentSound;
import svenhjol.charmony.ambient_sounds.client.features.environment.RepeatingEnvironmentSound;
import svenhjol.charmony.ambient_sounds.client.features.sound.SoundHandler;
import svenhjol.charmony.ambient_sounds.client.features.sound.SoundType;
import svenhjol.charmony.core.Charmony;
import svenhjol.charmony.core.helpers.WorldHelper;

import java.util.Optional;

public class Gravel implements SoundType<EnvironmentSound> {
    public final SoundEvent sound;

    public Gravel() {
        sound = SoundEvent.createVariableRangeEvent(Charmony.id("environment.gravel"));
    }

    public void addSounds(SoundHandler<EnvironmentSound> handler) {
        handler.getSounds().add(new RepeatingEnvironmentSound(handler.getPlayer()) {
            @Override
            public boolean isValidEnvironmentCondition() {
                Optional<BlockPos> optBlock = BlockPos.findClosestMatch(player.blockPosition(), 8, 4, pos -> {
                    var block = level.getBlockState(pos).getBlock();
                    return block == Blocks.GRAVEL;
                });

                return optBlock.isPresent();
            }

            @Override
            public boolean isValidPlayerCondition() {
                return !WorldHelper.isOutside(player)
                    && WorldHelper.isBelowSeaLevel(player);
            }

            @Nullable
            @Override
            public SoundEvent getSound() {
                return sound;
            }

            @Override
            public int getDelay() {
                return level.random.nextInt(450) + 400;
            }

            @Override
            public float getVolume() {
                return 0.85f;
            }
        });
    }
}
