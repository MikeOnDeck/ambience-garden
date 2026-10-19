package com.mikeondeck.ambiencegarden.client.sound;

import com.mikeondeck.ambiencegarden.core.base.Log;
import net.minecraft.sounds.SoundEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public final class AmbientPlayback {
    private static final Log LOG = new Log(AmbientSoundIds.NAMESPACE, "Playback");
    private static final AtomicBoolean LOGGED = new AtomicBoolean();

    private AmbientPlayback() {}

    public static void logFirstPlay(SoundEvent sound) {
        if (sound == null || !LOGGED.compareAndSet(false, true)) return;
        LOG.info("Playing ambient sound " + sound.location());
    }
}
