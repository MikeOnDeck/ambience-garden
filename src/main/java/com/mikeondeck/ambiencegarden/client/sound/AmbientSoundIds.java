package com.mikeondeck.ambiencegarden.client.sound;

import java.util.List;

/**
 * Sound event paths played by Ambience Garden.
 * The namespace must match {@code assets/ambience_garden/sounds.json}.
 * A {@code charmony:} id will load no audio, because that file is not in the jar.
 */
public final class AmbientSoundIds {
    public static final String NAMESPACE = "ambience_garden";

    public static final List<String> PATHS = List.of(
        "biome.beach.day",
        "biome.beach.night",
        "biome.caves.dripstone",
        "biome.caves.lush",
        "biome.caves.deep_dark",
        "biome.desert.day",
        "biome.desert.night",
        "biome.mountains.day",
        "biome.mountains.night",
        "biome.forest.day",
        "biome.forest.night",
        "biome.icy.day",
        "biome.icy.night",
        "biome.jungle.day",
        "biome.jungle.night",
        "biome.badlands.day",
        "biome.badlands.night",
        "biome.ocean.day",
        "biome.ocean.night",
        "biome.pale_garden.day",
        "biome.pale_garden.night",
        "biome.plains.day",
        "biome.plains.night",
        "biome.river",
        "biome.savanna.day",
        "biome.savanna.night",
        "biome.swamp.day",
        "biome.swamp.night",
        "biome.taiga.day",
        "biome.taiga.night",
        "biome.the_end",
        "environment.dry",
        "environment.bleak",
        "environment.night_plains",
        "environment.underground_water",
        "environment.cave_drone",
        "environment.deep_cave",
        "environment.alien",
        "environment.mineshaft",
        "environment.mansion",
        "environment.village",
        "environment.snowstorm",
        "environment.high",
        "environment.geode",
        "environment.gravel",
        "environment.deepslate"
    );

    private AmbientSoundIds() {}
}
