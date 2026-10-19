package com.mikeondeck.ambiencegarden.client.features.settings;

import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import com.mikeondeck.ambiencegarden.api.core.Configurable;
import com.mikeondeck.ambiencegarden.api.core.FeatureDefinition;
import com.mikeondeck.ambiencegarden.core.base.Mod;
import com.mikeondeck.ambiencegarden.core.base.SidedFeature;
import com.mikeondeck.ambiencegarden.api.core.Side;

import java.util.Arrays;

@FeatureDefinition(side = Side.Client, canBeDisabled = false, description = """
    Ambient sound settings.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "unused"})
public final class Settings extends SidedFeature {
    public static final String DEFAULT_AUDIO_CHANNEL = "ambient";
    public static final double DEFAULT_BIOME_VOLUME = 0.55d;
    public static final double DEFAULT_ENVIRONMENT_VOLUME = 0.55d;
    public static final int DEFAULT_CULL_SOUND_ABOVE_GROUND = 32;

    @Configurable(
        name = "Audio channel",
        description = """
            The channel that the mod uses for playing sounds. Defaults to 'ambient'.
            Options: music, record, weather, block, hostile, neutral, player, ambient, voice""",
        requireRestart = false
    )
    private static String audioChannel = DEFAULT_AUDIO_CHANNEL;

    @Configurable(name = "Biome volume scaling", description = """
        Affects the volume of all biome ambient sounds. 1.0 is full volume.""", requireRestart = false)
    private static double biomeVolumeScaling = DEFAULT_BIOME_VOLUME;

    @Configurable(name = "Environment volume scaling", description = """
        Affects the volume of all environmental ambient sounds. 1.0 is full volume.""", requireRestart = false)
    private static double environmentVolumeScaling = DEFAULT_ENVIRONMENT_VOLUME;

    @Configurable(
        name = "Height when silencing ambience",
        description = """
            Number of blocks above the ground that ambient sounds will be silenced.
            Set to zero to disable.""",
        requireRestart = false
    )
    private static int cullSoundAboveGround = DEFAULT_CULL_SOUND_ABOVE_GROUND;

    public Settings(Mod mod) {
        super(mod);
    }

    public static Settings feature() {
        return Mod.getSidedFeature(Settings.class);
    }

    public String audioChannel() {
        return audioChannel;
    }

    public double biomeVolumeScaling() {
        return Mth.clamp(biomeVolumeScaling, 0.0d, 1.0d);
    }

    public double environmentVolumeScaling() {
        return Mth.clamp(environmentVolumeScaling, 0.0d, 1.0d);
    }

    public int cullSoundAboveGround() {
        return Mth.clamp(cullSoundAboveGround, 0, 1024);
    }

    public void setAudioChannel(String audioChannel) {
        Settings.audioChannel = audioChannel == null ? DEFAULT_AUDIO_CHANNEL : audioChannel;
    }

    public void setBiomeVolumeScaling(double biomeVolumeScaling) {
        Settings.biomeVolumeScaling = biomeVolumeScaling;
    }

    public void setEnvironmentVolumeScaling(double environmentVolumeScaling) {
        Settings.environmentVolumeScaling = environmentVolumeScaling;
    }

    public void setCullSoundAboveGround(int cullSoundAboveGround) {
        Settings.cullSoundAboveGround = cullSoundAboveGround;
    }

    public void resetToDefaults() {
        audioChannel = DEFAULT_AUDIO_CHANNEL;
        biomeVolumeScaling = DEFAULT_BIOME_VOLUME;
        environmentVolumeScaling = DEFAULT_ENVIRONMENT_VOLUME;
        cullSoundAboveGround = DEFAULT_CULL_SOUND_ABOVE_GROUND;
    }

    public SoundSource soundSource() {
        SoundSource source;
        var key = audioChannel.toLowerCase();
        source = Arrays.stream(SoundSource.values())
            .filter(v -> v.getName().equals(key))
            .findFirst()
            .orElse(SoundSource.AMBIENT);

        return source;
    }
}
