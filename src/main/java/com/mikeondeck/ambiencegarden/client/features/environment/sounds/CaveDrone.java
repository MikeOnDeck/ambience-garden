package com.mikeondeck.ambiencegarden.client.features.environment.sounds;

import com.mikeondeck.ambiencegarden.client.features.environment.Environment;
import com.mikeondeck.ambiencegarden.client.features.environment.EnvironmentSound;
import com.mikeondeck.ambiencegarden.client.features.environment.LoopingEnvironmentSound;
import com.mikeondeck.ambiencegarden.client.features.sound.SoundHandler;
import com.mikeondeck.ambiencegarden.client.features.sound.SoundType;
import com.mikeondeck.ambiencegarden.client.sound.AmbientSoundEvents;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

public class CaveDrone implements SoundType<EnvironmentSound> {
    public final SoundEvent sound;

    public CaveDrone() {
        sound = AmbientSoundEvents.get("environment.cave_drone");
    }

    public void addSounds(SoundHandler<EnvironmentSound> handler) {
        handler.getSounds().add(new LoopingEnvironmentSound(handler.getPlayer()) {
            @Override
            public boolean isValidEnvironmentCondition() {
                var pos = player.blockPosition();
                var light = level.getMaxLocalRawBrightness(pos);

                if (!Environment.feature().validCaveDimensions().contains(level.dimension().identifier())) {
                    return false;
                }

                if (!level.canSeeSkyFromBelowWater(pos) && pos.getY() <= player.level().getSeaLevel()) {
                    return pos.getY() <= Environment.feature().caveDroneCutoff() || light <= Environment.feature().caveLightLevel();
                }

                return false;
            }

            @Override
            public boolean isValidPlayerCondition() {
                return !player.isUnderWater();
            }

            @Nullable
            @Override
            public SoundEvent getSound() {
                return sound;
            }
        });
    }
}
