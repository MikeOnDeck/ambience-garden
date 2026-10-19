package com.mikeondeck.ambiencegarden;

import com.mikeondeck.ambiencegarden.api.core.FeatureDefinition;
import com.mikeondeck.ambiencegarden.client.sound.AmbientSoundIds;
import com.mikeondeck.ambiencegarden.core.helpers.ConfigPaths;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Catches the silence bug: sound events must use the ambience_garden namespace,
 * every event referenced in code must exist in sounds.json, and every sounds.json
 * file must point at a fetched ogg. Also checks the ambient features default on.
 */
class SoundWiringTest {
    private static final Path ROOT = Path.of("").toAbsolutePath();
    private static final Path SOUNDS_JSON = ROOT.resolve("src/main/resources/assets/ambience_garden/sounds.json");
    private static final Path SOUND_ROOT = ROOT.resolve("src/main/resources/assets/ambience_garden/sounds");
    private static final Path FEATURES = ROOT.resolve("src/main/java/com/mikeondeck/ambiencegarden/client/features");
    private static final Pattern GET_CALL = Pattern.compile("AmbientSoundEvents\\.get\\(\"([^\"]+)\"\\)");
    private static final Pattern CHARMONY_ID = Pattern.compile("Charmony\\.id\\(");
    private static final Pattern JSON_KEY = Pattern.compile("(?m)^  \"([^\"]+)\"\\s*:\\s*\\{");
    private static final Pattern SOUND_NAME = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern BOOL_INIT = Pattern.compile("private static boolean (\\w+) = (true|false);");

    @Test
    void soundEventsUseAmbienceGardenNamespaceAndMatchSoundsJson() throws IOException {
        assertEquals("ambience_garden", AmbientSoundIds.NAMESPACE);
        assertFalse(AmbientSoundIds.PATHS.isEmpty());

        String soundsJson = Files.readString(SOUNDS_JSON);
        Set<String> jsonKeys = findAll(JSON_KEY, soundsJson);
        assertEquals(new LinkedHashSet<>(AmbientSoundIds.PATHS), jsonKeys, "sounds.json keys drifted from AmbientSoundIds.PATHS");

        Set<String> referenced = referencedSoundPaths();
        assertEquals(new LinkedHashSet<>(AmbientSoundIds.PATHS), referenced,
            "A sound event is created in code without a matching id, or an id is never used");

        String features = readTree(FEATURES);
        assertFalse(CHARMONY_ID.matcher(features).find(),
            "Sound code still builds charmony: ids. Those do not match assets/ambience_garden/sounds.json");

        String registrar = Files.readString(ROOT.resolve(
            "src/main/java/com/mikeondeck/ambiencegarden/client/sound/AmbientSoundEvents.java"));
        assertTrue(registrar.contains("AmbientSoundIds.NAMESPACE"),
            "Sound events must be registered with the ambience_garden namespace");
    }

    @Test
    void soundsJsonPointsAtFetchedOggFiles() throws IOException {
        assertTrue(Files.isDirectory(SOUND_ROOT),
            "Sound files are missing. Run scripts/fetch-sounds.sh before testing.");
        String soundsJson = Files.readString(SOUNDS_JSON);
        Matcher matcher = SOUND_NAME.matcher(soundsJson);
        int names = 0;
        while (matcher.find()) {
            names++;
            String name = matcher.group(1);
            assertTrue(name.startsWith(AmbientSoundIds.NAMESPACE + ":"),
                "Sound file id is not in the ambience_garden namespace: " + name);
            String relative = name.substring(AmbientSoundIds.NAMESPACE.length() + 1) + ".ogg";
            Path ogg = SOUND_ROOT.resolve(relative);
            assertTrue(Files.isRegularFile(ogg), "sounds.json points at a missing file: " + ogg);
        }
        assertTrue(names >= 100, "Expected the ambient library to reference at least 100 ogg files, found " + names);
    }

    @Test
    void ambientFeaturesAreEnabledByDefault() throws Exception {
        Object defaultEnabled = FeatureDefinition.class.getMethod("enabledByDefault").getDefaultValue();
        assertEquals(Boolean.TRUE, defaultEnabled);

        for (String name : List.of("Biomes.java", "Environment.java", "Settings.java")) {
            String source = findFeatureSource(name);
            assertFalse(source.contains("enabledByDefault = false"), name + " is disabled by default");
            assertFalse(source.contains("enabledByDefault=false"), name + " is disabled by default");
        }

        String environment = findFeatureSource("Environment.java");
        Matcher matcher = BOOL_INIT.matcher(environment);
        int flags = 0;
        while (matcher.find()) {
            flags++;
            assertEquals("true", matcher.group(2), "Environment sound " + matcher.group(1) + " defaults off");
        }
        assertTrue(flags >= 15, "Expected the environment sound toggles, found " + flags);
    }

    @Test
    void configLivesUnderAmbienceGardenDirectory() {
        Path path = ConfigPaths.resolve(Path.of("config"), "ambience_garden", "client");
        assertEquals(Path.of("config", "ambience_garden", "ambience_garden-client.toml"), path);
        assertEquals("ambience_garden", ConfigPaths.DIRECTORY);
    }

    @Test
    void clientEntrypointAndSettingsHooksAreWired() throws IOException {
        String modJson = Files.readString(ROOT.resolve("src/main/resources/fabric.mod.json"));
        assertTrue(modJson.contains("com.mikeondeck.ambiencegarden.client.ClientInitializer"));
        assertTrue(modJson.contains("com.mikeondeck.ambiencegarden.integration.ModMenuIntegration"));
        assertTrue(modJson.contains("\"suggests\""));
        assertTrue(modJson.contains("modmenu"));
        assertFalse(modJson.contains("SoundOptionsScreenMixin"),
            "The Music & Sounds button must not depend on a mixin that shadows a superclass field");

        String hook = Files.readString(ROOT.resolve(
            "src/main/java/com/mikeondeck/ambiencegarden/client/gui/SoundOptionsHook.java"));
        assertTrue(hook.contains("ScreenEvents.AFTER_INIT"));
        assertTrue(hook.contains("SoundOptionsScreen"));
        assertTrue(hook.contains("AmbienceGardenSettingsScreen"));

        String widener = Files.readString(ROOT.resolve("src/main/resources/ambience_garden.accesswidener"));
        assertTrue(widener.contains("OptionsSubScreen list"),
            "The options list field must be widened so the button can be added without a mixin");

        String initializer = Files.readString(ROOT.resolve(
            "src/main/java/com/mikeondeck/ambiencegarden/client/ClientInitializer.java"));
        assertTrue(initializer.contains("SoundOptionsHook.register()"));
        assertTrue(initializer.contains("AmbientSoundEvents.registerAll()"),
            "Sound events must be registered during client init, before features construct them");
        assertTrue(initializer.contains("addSidedFeature(Biomes.class)"));
        assertTrue(initializer.contains("addSidedFeature(Environment.class)"));
        assertTrue(initializer.contains("addSidedFeature(Settings.class)"));
    }

    private static Set<String> referencedSoundPaths() throws IOException {
        Set<String> paths = new LinkedHashSet<>();
        try (Stream<Path> files = Files.walk(FEATURES)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                Matcher matcher = GET_CALL.matcher(Files.readString(file));
                while (matcher.find()) {
                    paths.add(matcher.group(1));
                }
            }
        }
        return paths;
    }

    private static String readTree(Path root) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (Stream<Path> files = Files.walk(root)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                builder.append(Files.readString(file)).append('\n');
            }
        }
        return builder.toString();
    }

    private static String findFeatureSource(String fileName) throws IOException {
        try (Stream<Path> files = Files.walk(FEATURES)) {
            return files.filter(path -> path.getFileName().toString().equals(fileName))
                .findFirst()
                .map(path -> {
                    try {
                        return Files.readString(path);
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                })
                .orElseThrow(() -> new IllegalStateException("Missing " + fileName));
        }
    }

    private static Set<String> findAll(Pattern pattern, String text) {
        Set<String> found = new LinkedHashSet<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            found.add(matcher.group(1));
        }
        return found;
    }
}
