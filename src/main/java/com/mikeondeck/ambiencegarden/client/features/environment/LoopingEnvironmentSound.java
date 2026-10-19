package com.mikeondeck.ambiencegarden.client.features.environment;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.world.entity.player.Player;
import com.mikeondeck.ambiencegarden.client.features.sound.LoopingSound;

import java.util.ConcurrentModificationException;

public abstract class LoopingEnvironmentSound extends EnvironmentSound {
    protected int numberOfExceptions;
    protected LoopingSound soundInstance;

    public LoopingEnvironmentSound(Player player) {
        super(player);
        numberOfExceptions = 0;
    }

    public AbstractTickableSoundInstance getSoundInstance() {
        return soundInstance;
    }

    @Override
    public void tick() {
        boolean nowValid = isValid();

        if (isValid && !nowValid) isValid = false;
        if (!isValid && nowValid) isValid = true;

        if (isValid) {
            var volume = (float)(getVolume() * getVolumeScaling());
            if (!isPlaying()) {
                soundInstance = new LoopingSound(player, getSound(), volume, getPitch(), p -> isValid);
                try {
                    getSoundManager().play(this.soundInstance);
                } catch (ConcurrentModificationException e) {
                    if (numberOfExceptions++ < 3) { // Don't spam too much.
                        Environment.feature().log().debug("Exception in tick");
                    }
                }
            } else if (soundInstance != null && soundInstance.maxVolume != volume) {
                if (soundInstance.maxVolume < volume) {
                    soundInstance.maxVolume = Math.min(volume, soundInstance.maxVolume + 0.005f);
                } else {
                    soundInstance.maxVolume = Math.max(volume, soundInstance.maxVolume - 0.005f);
                }
            }
        }
    }
}
