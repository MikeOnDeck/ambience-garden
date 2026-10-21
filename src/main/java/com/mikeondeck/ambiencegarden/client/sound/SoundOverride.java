package com.mikeondeck.ambiencegarden.client.sound;

import com.mikeondeck.ambiencegarden.client.features.biomes.Biomes;
import com.mikeondeck.ambiencegarden.client.features.environment.Environment;
import com.mikeondeck.ambiencegarden.client.features.sound.LoopingSound;
import com.mikeondeck.ambiencegarden.client.features.sound.SoundHandler;
import com.mikeondeck.ambiencegarden.client.features.sound.SoundInstance;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sits between automatic sound selection and playback.
 * The debug tab writes slider volumes here. The overlay reads {@link #playbackLines()}.
 * Nothing in this class is written to config.
 */
public final class SoundOverride {
    private static boolean active;
    private static boolean sawPlayer;
    private static final Map<String, Float> volumes = new LinkedHashMap<>();
    private static final Map<String, LoopingSound> playing = new LinkedHashMap<>();

    private SoundOverride() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(SoundOverride::tick);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> setActive(false));
    }

    public static boolean isActive() {
        return active;
    }

    /**
     * Turning override on freezes whatever is audible right now into the sliders
     * and suspends selection. Turning it off drops those values and lets the
     * normal crossfade pick the area again.
     */
    public static void setActive(boolean on) {
        if (active == on) {
            return;
        }
        if (on) {
            volumes.clear();
            for (SoundCatalog.Entry entry : SoundCatalog.all()) {
                volumes.put(entry.path(), clamp(handlerVolume(entry.path())));
            }
            active = true;
            suspendHandlers();
            var player = Minecraft.getInstance().player;
            if (player != null) {
                for (SoundCatalog.Entry entry : SoundCatalog.all()) {
                    apply(player, entry.path(), volumes.getOrDefault(entry.path(), 0.0f));
                }
            }
        } else {
            active = false;
            volumes.clear();
            stopOverrideSounds();
        }
    }

    public static void setVolume(String path, float volume) {
        if (!active) {
            return;
        }
        float clamped = clamp(volume);
        volumes.put(path, clamped);
        var player = Minecraft.getInstance().player;
        if (player != null) {
            apply(player, path, clamped);
        }
    }

    public static float sliderVolume(String path) {
        if (active) {
            return volumes.getOrDefault(path, 0.0f);
        }
        return handlerVolume(path);
    }

    public static float liveVolume(String path) {
        if (active) {
            LoopingSound loop = playing.get(path);
            if (loop != null && !loop.isStopped()) {
                return loop.outputVolume();
            }
            return 0.0f;
        }
        return handlerVolume(path);
    }

    public static List<PlaybackListing.Entry> playbackLines() {
        var rows = new ArrayList<PlaybackListing.Entry>();
        if (active) {
            for (SoundCatalog.Entry entry : SoundCatalog.all()) {
                LoopingSound loop = playing.get(entry.path());
                float volume = 0.0f;
                boolean fadingIn = false;
                if (loop != null && !loop.isStopped()) {
                    volume = loop.outputVolume();
                    fadingIn = loop.isFadingIn();
                }
                rows.add(new PlaybackListing.Entry(entry.id(), volume, fadingIn));
            }
        } else {
            forEachHandlerSound(sound -> rows.add(describe(sound)));
        }
        return PlaybackListing.visibleSorted(rows);
    }

    /**
     * Called from the sound handlers while override is on so they do not
     * start or crossfade anything underneath the held volumes.
     */
    public static void keepAutomaticSuspended(SoundHandler<?> handler) {
        if (!active || handler == null) {
            return;
        }
        stopHandler(handler);
    }

    private static void tick(Minecraft client) {
        boolean hasPlayer = client.player != null && client.level != null;
        if (sawPlayer && !hasPlayer && active) {
            setActive(false);
        }
        sawPlayer = hasPlayer;
        if (!active || !hasPlayer || client.isPaused()) {
            return;
        }
        suspendHandlers();
        for (SoundCatalog.Entry entry : SoundCatalog.all()) {
            apply(client.player, entry.path(), volumes.getOrDefault(entry.path(), 0.0f));
        }
    }

    private static void apply(Player player, String path, float volume) {
        LoopingSound existing = playing.get(path);
        if (volume <= 0.0f) {
            if (existing != null) {
                silence(existing);
                playing.remove(path);
            }
            return;
        }
        if (existing != null && (existing.isStopped() || !existing.follows(player) || !Minecraft.getInstance().getSoundManager().isActive(existing))) {
            silence(existing);
            existing = null;
            playing.remove(path);
        }
        if (existing == null) {
            SoundEvent event = AmbientSoundEvents.get(path);
            var loop = new LoopingSound(player, event, volume, 1.0f, ignored -> active);
            loop.holdAt(volume);
            try {
                Minecraft.getInstance().getSoundManager().play(loop);
            } catch (RuntimeException ignored) {
                return;
            }
            playing.put(path, loop);
        } else {
            existing.holdAt(volume);
        }
    }

    private static void suspendHandlers() {
        var biomes = Biomes.feature();
        if (biomes != null && biomes.handlers.activeHandler() != null) {
            stopHandler(biomes.handlers.activeHandler());
        }
        var environment = Environment.feature();
        if (environment != null && environment.handlers.activeHandler() != null) {
            stopHandler(environment.handlers.activeHandler());
        }
    }

    private static void stopHandler(SoundHandler<?> handler) {
        for (SoundInstance sound : handler.getSounds()) {
            if (sound.getSoundInstance() != null) {
                sound.stop();
            }
        }
    }

    private static void stopOverrideSounds() {
        for (LoopingSound loop : playing.values()) {
            silence(loop);
        }
        playing.clear();
    }

    private static void silence(LoopingSound loop) {
        loop.silence();
        try {
            Minecraft.getInstance().getSoundManager().stop(loop);
        } catch (RuntimeException ignored) {
            // The sound engine may already have dropped this instance.
        }
    }

    private static float handlerVolume(String path) {
        float[] found = new float[] {0.0f};
        forEachHandlerSound(sound -> {
            if (!matches(sound, path)) {
                return;
            }
            found[0] = Math.max(found[0], outputVolume(sound));
        });
        return found[0];
    }

    private static PlaybackListing.Entry describe(SoundInstance sound) {
        String id = idOf(sound);
        var instance = sound.getSoundInstance();
        float volume = 0.0f;
        boolean fadingIn = false;
        if (instance instanceof LoopingSound looping) {
            if (sound.isPlaying() || looping.isFadingIn()) {
                volume = looping.outputVolume();
                fadingIn = looping.isFadingIn();
            }
        } else if (instance != null && sound.isPlaying()) {
            volume = instance.getVolume();
        }
        return new PlaybackListing.Entry(id, volume, fadingIn);
    }

    private static float outputVolume(SoundInstance sound) {
        var instance = sound.getSoundInstance();
        if (instance instanceof LoopingSound looping) {
            if (sound.isPlaying() || looping.isFadingIn()) {
                return looping.outputVolume();
            }
            return 0.0f;
        }
        if (instance != null && sound.isPlaying()) {
            return instance.getVolume();
        }
        return 0.0f;
    }

    private static void forEachHandlerSound(java.util.function.Consumer<SoundInstance> consumer) {
        var biomes = Biomes.feature();
        if (biomes != null && biomes.handlers.activeHandler() != null) {
            biomes.handlers.activeHandler().getSounds().forEach(consumer);
        }
        var environment = Environment.feature();
        if (environment != null && environment.handlers.activeHandler() != null) {
            environment.handlers.activeHandler().getSounds().forEach(consumer);
        }
    }

    private static boolean matches(SoundInstance sound, String path) {
        SoundEvent event = sound.getSound();
        if (event == null) {
            return false;
        }
        var id = event.location();
        return AmbientSoundIds.NAMESPACE.equals(id.getNamespace()) && path.equals(id.getPath());
    }

    private static String idOf(SoundInstance sound) {
        SoundEvent event = sound.getSound();
        if (event == null) {
            return sound.getClass().getSimpleName();
        }
        var id = event.location();
        return id.getNamespace() + ":" + id.getPath();
    }

    private static float clamp(float volume) {
        if (volume < 0.0f) {
            return 0.0f;
        }
        if (volume > 1.0f) {
            return 1.0f;
        }
        return volume;
    }
}
