package svenhjol.charmony.ambient_sounds.helpers;

import net.minecraft.sounds.SoundSource;
import svenhjol.charmony.ambient_sounds.client.features.settings.Settings;

public final class SettingsHelper {
    public static SoundSource soundSource() {
        return Settings.feature().soundSource();
    }

    public static double biomeVolumeScaling() {
        return Settings.feature().biomeVolumeScaling();
    }

    public static double environmentVolumeScaling() {
        return Settings.feature().environmentVolumeScaling();
    }

    public static int cullSoundAboveGround() {
        return Settings.feature().cullSoundAboveGround();
    }
}
