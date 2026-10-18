package com.mikeondeck.ambiencegarden.client.gui;

import com.mikeondeck.ambiencegarden.AmbientSoundsMod;
import com.mikeondeck.ambiencegarden.client.features.biomes.Biomes;
import com.mikeondeck.ambiencegarden.client.features.environment.Environment;

public final class LiveSettings {
    private LiveSettings() {}

    public static void save() {
        AmbientSoundsMod.instance().config().write();
    }

    public static void reloadSounds() {
        Biomes.feature().handlers.reload();
        Environment.feature().handlers.reload();
    }
}
