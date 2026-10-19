package com.mikeondeck.ambiencegarden.client.features.sound;

/**
 * Implemented by all ambient sounds.
 */
public interface SoundType<T extends SoundInstance> {
    void addSounds(SoundHandler<T> handler);
}
