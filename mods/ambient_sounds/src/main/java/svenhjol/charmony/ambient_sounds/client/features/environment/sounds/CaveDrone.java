package svenhjol.charmony.ambient_sounds.client.features.environment.sounds;

import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;
import svenhjol.charmony.ambient_sounds.client.features.environment.Environment;
import svenhjol.charmony.ambient_sounds.client.features.environment.EnvironmentSound;
import svenhjol.charmony.ambient_sounds.client.features.environment.LoopingEnvironmentSound;
import svenhjol.charmony.ambient_sounds.client.features.sound.SoundHandler;
import svenhjol.charmony.ambient_sounds.client.features.sound.SoundType;
import svenhjol.charmony.core.Charmony;

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

                if (!Environment.feature().validCaveDimensions().contains(level.dimension().location())) {
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
