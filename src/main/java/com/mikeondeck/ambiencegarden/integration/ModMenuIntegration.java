package com.mikeondeck.ambiencegarden.integration;

import com.mikeondeck.ambiencegarden.client.gui.AmbienceGardenSettingsScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Loaded only when Mod Menu asks for the {@code modmenu} entrypoint.
 * The class is never touched if Mod Menu is not installed.
 */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return AmbienceGardenSettingsScreen::new;
    }
}
