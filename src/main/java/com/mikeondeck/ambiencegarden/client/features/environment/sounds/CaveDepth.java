package com.mikeondeck.ambiencegarden.client.features.environment.sounds;

import com.mikeondeck.ambiencegarden.client.features.environment.Environment;
import com.mikeondeck.ambiencegarden.client.features.environment.EnvironmentSound;
import com.mikeondeck.ambiencegarden.client.features.environment.LoopingEnvironmentSound;
import com.mikeondeck.ambiencegarden.client.features.sound.SoundHandler;
import com.mikeondeck.ambiencegarden.client.features.sound.SoundType;
import com.mikeondeck.ambiencegarden.client.sound.AmbientSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.Biomes;
import org.jetbrains.annotations.Nullable;

public class CaveDepth implements SoundType<EnvironmentSound> {
    public final SoundEvent sound;

    public CaveDepth() {
        sound = AmbientSoundEvents.get("environment.deep_cave");
    }

    @Override
    public void addSounds(SoundHandler<EnvironmentSound> handler) {
        handler.getSounds().add(new LoopingEnvironmentSound(handler.getPlayer()) {
            @Override
            public boolean isValidEnvironmentCondition() {
                var pos = player.blockPosition();

                // Don't play this if the player is in the Deep Dark, the combined sounds are too intense.
                var key = getBiomeKey(pos);
                if (key == Biomes.DEEP_DARK) {
                    return false;
                }

                if (!Environment.feature().validCaveDimensions().contains(level.dimension().identifier())) {
                    return false;
                }

                var light = level.getMaxLocalRawBrightness(pos);
                var bottom = level.getMinY() < 0 ? 0 : 32;
                return !level.canSeeSkyFromBelowWater(pos)
                    && pos.getY() <= bottom
                    && light < Environment.feature().caveLightLevel();
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
