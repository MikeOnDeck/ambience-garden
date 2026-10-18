package com.mikeondeck.ambiencegarden.helpers;

import net.minecraft.sounds.SoundSource;
import com.mikeondeck.ambiencegarden.client.features.settings.Settings;

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
