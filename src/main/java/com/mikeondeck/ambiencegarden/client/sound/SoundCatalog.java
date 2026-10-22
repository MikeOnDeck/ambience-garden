package com.mikeondeck.ambiencegarden.client.sound;

import java.util.ArrayList;
import java.util.List;

/**
 * Every sound the mod can play, grouped the way the banks are registered.
 * The debug tab renders one slider per entry. Ids are {@code namespace:path}.
 */
public final class SoundCatalog {
    public enum Group {
        BIOME,
        ENVIRONMENT
    }

    public record Entry(Group group, String bank, String label, String path) {
        public String id() {
            return AmbientSoundIds.NAMESPACE + ":" + path;
        }
    }

    public record Bank(Group group, String name, List<Entry> entries) {}

    private static final List<Bank> BANKS = List.of(
        biome("Badlands", sound("Day", "biome.badlands.day"), sound("Night", "biome.badlands.night")),
        biome("Beach", sound("Day", "biome.beach.day"), sound("Night", "biome.beach.night")),
        biome("Caves", sound("Deep Dark", "biome.caves.deep_dark"), sound("Dripstone", "biome.caves.dripstone"), sound("Lush", "biome.caves.lush")),
        biome("Desert", sound("Day", "biome.desert.day"), sound("Night", "biome.desert.night")),
        biome("Forest", sound("Day", "biome.forest.day"), sound("Night", "biome.forest.night")),
        biome("Icy", sound("Day", "biome.icy.day"), sound("Night", "biome.icy.night")),
        biome("Jungle", sound("Day", "biome.jungle.day"), sound("Night", "biome.jungle.night")),
        biome("Mountains", sound("Day", "biome.mountains.day"), sound("Night", "biome.mountains.night")),
        biome("Ocean", sound("Day", "biome.ocean.day"), sound("Night", "biome.ocean.night")),
        biome("Pale Garden", sound("Day", "biome.pale_garden.day"), sound("Night", "biome.pale_garden.night")),
        biome("Plains", sound("Day", "biome.plains.day"), sound("Night", "biome.plains.night")),
        biome("River", sound("River", "biome.river")),
        biome("Savanna", sound("Day", "biome.savanna.day"), sound("Night", "biome.savanna.night")),
        biome("Swamp", sound("Day", "biome.swamp.day"), sound("Night", "biome.swamp.night")),
        biome("Taiga", sound("Day", "biome.taiga.day"), sound("Night", "biome.taiga.night")),
        biome("The End", sound("The End", "biome.the_end")),
        environment("Alien", "environment.alien"),
        environment("Bleak", "environment.bleak"),
        environment("Cave Depth", "environment.deep_cave"),
        environment("Cave Drone", "environment.cave_drone"),
        environment("Deepslate", "environment.deepslate"),
        environment("Dry", "environment.dry"),
        environment("Geode", "environment.geode"),
        environment("Gravel", "environment.gravel"),
        environment("High Places", "environment.high"),
        environment("Mansions", "environment.mansion"),
        environment("Mineshafts", "environment.mineshaft"),
        environment("Night Plains", "environment.night_plains"),
        environment("Snowstorm", "environment.snowstorm"),
        environment("Underground Water", "environment.underground_water"),
        environment("Villages", "environment.village")
    );

    private SoundCatalog() {}

    public static List<Bank> banks() {
        return BANKS;
    }

    public static List<Entry> all() {
        var all = new ArrayList<Entry>();
        for (Bank bank : BANKS) {
            all.addAll(bank.entries());
        }
        return List.copyOf(all);
    }

    public static Entry find(String path) {
        for (Entry entry : all()) {
            if (entry.path().equals(path)) {
                return entry;
            }
        }
        return null;
    }

    private static Bank biome(String name, Named... sounds) {
        return bank(Group.BIOME, name, sounds);
    }

    private static Bank environment(String name, String path) {
        return bank(Group.ENVIRONMENT, name, sound(name, path));
    }

    private static Bank bank(Group group, String name, Named... sounds) {
        var entries = new ArrayList<Entry>();
        for (Named sound : sounds) {
            entries.add(new Entry(group, name, sound.label(), sound.path()));
        }
        return new Bank(group, name, List.copyOf(entries));
    }

    private static Named sound(String label, String path) {
        return new Named(label, path);
    }

    private record Named(String label, String path) {}
}
