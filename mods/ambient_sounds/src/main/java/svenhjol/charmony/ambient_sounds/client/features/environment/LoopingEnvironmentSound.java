package svenhjol.charmony.ambient_sounds.client.features.environment;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.ambient_sounds.client.features.sound.LoopingSound;

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

        if (isValid && !isPlaying()) {
            soundInstance = new LoopingSound(player, getSound(), (float)(getVolume() * getVolumeScaling()), getPitch(), p -> isValid);
            try {
                getSoundManager().play(this.soundInstance);
            } catch (ConcurrentModificationException e) {
                if (numberOfExceptions++ < 3) { // Don't spam too much.
                    Environment.feature().log().debug("Exception in tick");
                }
            }
        }
    }
}
