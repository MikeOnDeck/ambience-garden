package charmony.ambient_sounds.client.features.environment.sounds;

import charmony.ambient_sounds.client.features.environment.Environment;
import charmony.ambient_sounds.client.features.environment.EnvironmentSound;
import charmony.ambient_sounds.client.features.environment.LoopingEnvironmentSound;
import charmony.ambient_sounds.client.features.sound.SoundHandler;
import charmony.ambient_sounds.client.features.sound.SoundType;
import charmony.core.Charmony;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

public class CaveDrone implements SoundType<EnvironmentSound> {
    public final SoundEvent sound;

    public CaveDrone() {
        sound = SoundEvent.createVariableRangeEvent(Charmony.id("environment.cave_drone"));
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
