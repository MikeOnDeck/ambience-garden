package svenhjol.charmony.ambient_sounds.client.features.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.ambient_sounds.helpers.SettingsHelper;

import java.util.function.Predicate;

public class LoopingSound extends AbstractTickableSoundInstance {
    public static final int FADE_TIME = 140;

    private final Player player;
    private final Predicate<Player> predicate;
    private int longTicks;
    public float maxVolume;

    public LoopingSound(Player player, SoundEvent sound, float volume, float pitch, Predicate<Player> predicate) {
        super(sound, SettingsHelper.soundSource(), RandomSource.create());

        this.maxVolume = volume;
        this.player = player;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.01f;
        this.pitch = pitch;
        this.relative = true;
        this.predicate = predicate;
        this.longTicks = -50;
    }

    @Override
    public void tick() {
        if (player.isAlive()) {

            if (predicate.test(player)) {
                ++longTicks;
            } else {
                longTicks -= 1;
            }

            longTicks = Math.min(longTicks, FADE_TIME);
            volume = Math.max(0.0f, Math.min((float) longTicks / FADE_TIME, 1.0f)) * maxVolume;

            var donePlaying = stopped;
            if (!donePlaying && volume == 0.0f && longTicks < -100)
                stopped = true;

        } else {
            stopped = true;
        }
    }
}
