package com.mikeondeck.ambiencegarden.client.features.environment.sounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import com.mikeondeck.ambiencegarden.client.features.environment.EnvironmentSound;
import com.mikeondeck.ambiencegarden.client.features.environment.RepeatingEnvironmentSound;
import com.mikeondeck.ambiencegarden.client.features.sound.SoundHandler;
import com.mikeondeck.ambiencegarden.client.features.sound.SoundType;
import com.mikeondeck.ambiencegarden.core.Charmony;
import com.mikeondeck.ambiencegarden.core.helpers.WorldHelper;

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
