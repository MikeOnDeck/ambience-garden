package com.mikeondeck.ambiencegarden.core.helpers;

import java.nio.file.Path;

public final class ConfigPaths {
    public static final String DIRECTORY = "ambience_garden";

    private ConfigPaths() {}

    public static Path resolve(Path configDir, String modId, String side) {
        return configDir.resolve(DIRECTORY).resolve(modId + "-" + side + ".toml");
    }
}
