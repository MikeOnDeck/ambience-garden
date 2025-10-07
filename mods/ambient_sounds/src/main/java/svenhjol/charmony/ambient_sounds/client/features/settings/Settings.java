package svenhjol.charmony.ambient_sounds.client.features.settings;

import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

import java.util.Arrays;

@FeatureDefinition(side = Side.Client, canBeDisabled = false, description = """
    Ambient sound settings.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "unused"})
public final class Settings extends SidedFeature {
    @Configurable(
        name = "Audio channel",
        description = """
            The channel that the mod uses for playing sounds. Defaults to 'ambient'.
            Options: music, record, weather, block, hostile, neutral, player, ambient, voice"""
    )
    private static String audioChannel = "ambient";

    @Configurable(name = "Biome volume scaling", description = """
        Affects the volume of all biome ambient sounds. 1.0 is full volume.""", requireRestart = false)
    private static double biomeVolumeScaling = 0.55d;

    @Configurable(name = "Environment volume scaling", description = """
        Affects the volume of all environmental ambient sounds. 1.0 is full volume.""", requireRestart = false)
    private static double environmentVolumeScaling = 0.55d;

    @Configurable(
        name = "Height when silencing ambience",
        description = """
            Number of blocks above the ground that ambient sounds will be silenced.
            Set to zero to disable.""",
        requireRestart = false
    )
    private static int cullSoundAboveGround = 32;

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
