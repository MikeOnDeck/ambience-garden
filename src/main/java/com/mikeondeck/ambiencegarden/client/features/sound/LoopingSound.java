package com.mikeondeck.ambiencegarden.client.features.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import com.mikeondeck.ambiencegarden.client.sound.AmbientPlayback;
import com.mikeondeck.ambiencegarden.helpers.SettingsHelper;

import java.util.function.Predicate;

public class LoopingSound extends AbstractTickableSoundInstance {
    public static final int FADE_TIME = 140;

    private final Player player;
    private final Predicate<Player> predicate;
    private int longTicks;
    private boolean held;
    private boolean fadingIn;
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
        AmbientPlayback.logFirstPlay(sound);
    }

    /**
     * Hold this loop at exactly {@code target} with no fade. Used while the
     * debug override is driving playback.
     */
    public void holdAt(float target) {
        this.held = true;
        this.fadingIn = false;
        this.maxVolume = Math.max(0.0f, target);
        this.volume = this.maxVolume;
        if (this.maxVolume <= 0.0f) {
            this.longTicks = -200;
            this.stopped = true;
        } else {
            this.longTicks = FADE_TIME;
            this.stopped = false;
        }
    }

    public void releaseHold() {
        this.held = false;
    }

    public void silence() {
        this.held = false;
        this.fadingIn = false;
        this.volume = 0.0f;
        this.maxVolume = 0.0f;
        this.longTicks = -200;
        this.stopped = true;
    }

    public float outputVolume() {
        return this.volume;
    }

    public boolean isFadingIn() {
        return fadingIn && !stopped;
    }

    public boolean isStopped() {
        return stopped;
    }

    public boolean follows(Player other) {
        return this.player == other;
    }

    @Override
    public void tick() {
        if (!player.isAlive()) {
            stopped = true;
            fadingIn = false;
            return;
        }

        if (held) {
            volume = maxVolume;
            fadingIn = false;
            if (maxVolume <= 0.0f) {
                stopped = true;
            }
            return;
        }

        int before = longTicks;
        if (predicate.test(player)) {
            ++longTicks;
        } else {
            longTicks -= 1;
        }

        longTicks = Math.min(longTicks, FADE_TIME);
        volume = Math.max(0.0f, Math.min((float) longTicks / FADE_TIME, 1.0f)) * maxVolume;

        var donePlaying = stopped;
        if (!donePlaying && volume == 0.0f && longTicks < -100) {
            stopped = true;
        }
        fadingIn = !stopped && longTicks < FADE_TIME && longTicks > before;
    }
}
