package com.mikeondeck.ambiencegarden.client.debug;

import com.mikeondeck.ambiencegarden.client.features.biomes.BiomeSound;
import com.mikeondeck.ambiencegarden.client.features.biomes.Biomes;
import com.mikeondeck.ambiencegarden.client.features.biomes.SurfaceBiomeSound;
import com.mikeondeck.ambiencegarden.client.features.environment.EnvironmentSound;
import com.mikeondeck.ambiencegarden.client.features.sound.LoopingSound;
import com.mikeondeck.ambiencegarden.client.features.sound.SoundInstance;
import com.mikeondeck.ambiencegarden.core.helpers.WorldHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class AmbientSoundDiagnostics {
    private AmbientSoundDiagnostics() {}

    public record SoundLine(
        String id,
        boolean eligible,
        boolean playing,
        float effectiveVolume,
        String fadeState,
        String reason
    ) {}

    public static SoundLine describe(SoundInstance sound) {
        var id = label(sound);
        boolean eligible = safeEligible(sound);
        boolean playing = sound.isPlaying();
        float effective = effectiveVolume(sound);
        String fade = fadeState(sound);
        String reason = eligible ? "ok" : ineligibleReason(sound);
        return new SoundLine(id, eligible, playing, effective, fade, reason);
    }

    private static boolean safeEligible(SoundInstance sound) {
        try {
            return sound.isValid();
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static String label(SoundInstance sound) {
        SoundEvent event = sound.getSound();
        if (event != null) {
            Identifier location = event.location();
            return location.getNamespace() + ":" + location.getPath();
        }
        return sound.getClass().getSimpleName();
    }

    private static float effectiveVolume(SoundInstance sound) {
        double scale = sound.getVolumeScaling();
        float base = sound.getVolume();
        float blend = sound instanceof BiomeSound biome ? biome.debugBlendScaling() : 1.0f;
        float loopMul = sound.isPlaying() ? readLoopVolume(sound) : 1.0f;
        return (float) (base * scale * blend * loopMul);
    }

    private static float readLoopVolume(SoundInstance sound) {
        var instance = sound.getSoundInstance();
        if (!(instance instanceof LoopingSound looping)) {
            return 1.0f;
        }
        if (sound.getSound() == null) {
            return 0f;
        }
        try {
            return looping.getVolume();
        } catch (RuntimeException e) {
            return looping.maxVolume;
        }
    }

    private static String fadeState(SoundInstance sound) {
        var instance = sound.getSoundInstance();
        if (!(instance instanceof LoopingSound looping)) {
            return playingOrSilent(sound);
        }
        float current = sound.isPlaying() ? readLoopVolume(sound) : 0f;
        return String.format("cur=%.3f max=%.3f", current, looping.maxVolume);
    }

    private static String playingOrSilent(SoundInstance sound) {
        return sound.isPlaying() ? "playing" : "silent";
    }

    private static String ineligibleReason(SoundInstance sound) {
        var client = Minecraft.getInstance();
        var player = sound.getPlayer();
        var reasons = new ArrayList<String>();

        if (client.level == null || sound.getLevel() == null) {
            reasons.add("no level");
        }
        if (player == null || !player.isAlive()) {
            reasons.add("player");
        }

        if (sound instanceof BiomeSound biomeSound) {
            if (!Biomes.feature().validDimensions().contains(sound.getLevel().dimension().identifier())) {
                reasons.add("dimension");
            }
            if (!biomeSound.isValidPlayerCondition()) {
                if (WorldHelper.isThundering(player) && biomeSound instanceof SurfaceBiomeSound surface && !surface.debugPlayWhenThundering()) {
                    reasons.add("thundering");
                }
                if (!WorldHelper.isOutside(player)) {
                    reasons.add("not outdoor");
                }
            }
            if (reasons.isEmpty()) {
                reasons.add("biome/blend");
            }
        } else if (sound instanceof EnvironmentSound env) {
            if (player != null && player.isUnderWater() && !env.debugPlayUnderWater()) {
                reasons.add("underwater");
            }
            if (!env.isValidEnvironmentCondition()) {
                reasons.add("environment");
            }
            if (!env.isValidPlayerCondition()) {
                reasons.add("player/env");
            }
            if (reasons.isEmpty()) {
                reasons.add("environment");
            }
        } else if (!sound.isValidPlayerCondition()) {
            reasons.add("player condition");
        }

        return String.join(", ", reasons);
    }
}
