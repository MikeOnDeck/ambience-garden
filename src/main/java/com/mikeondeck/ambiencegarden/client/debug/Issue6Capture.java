package com.mikeondeck.ambiencegarden.client.debug;

import com.mikeondeck.ambiencegarden.AmbientSoundsMod;
import com.mikeondeck.ambiencegarden.client.features.biomes.Biomes;
import com.mikeondeck.ambiencegarden.client.gui.AmbienceGardenSettingsScreen;
import com.mikeondeck.ambiencegarden.client.sound.PlaybackListing;
import com.mikeondeck.ambiencegarden.client.sound.SoundOverride;
import com.mikeondeck.ambiencegarden.core.base.Log;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Headless capture for the debug overlay filter and the Debug tab override.
 * Set {@code AMBIENCE_GARDEN_VERIFY_DEBUG_OVERLAY=issue6}.
 */
public final class Issue6Capture {
    private static final Log LOG = new Log(AmbientSoundsMod.ID, "Issue6");
    private static final String WORLD_NAME = "ambience_garden_issue6";
    private static volatile String pendingScreenshot;
    private static volatile Runnable afterScreenshot;
    private static int captureDelayTicks;
    private static final AtomicBoolean screenshotGrabScheduled = new AtomicBoolean();

    private Issue6Capture() {}

    public static boolean enabled() {
        String raw = System.getProperty("ambience_garden.verifyDebugOverlay");
        if (raw == null || raw.isBlank()) {
            raw = System.getenv("AMBIENCE_GARDEN_VERIFY_DEBUG_OVERLAY");
        }
        return "issue6".equalsIgnoreCase(raw);
    }

    public static void endClientTick() {
        if (!enabled() || pendingScreenshot == null) {
            return;
        }
        if (captureDelayTicks > 0) {
            captureDelayTicks--;
            return;
        }
        if (!screenshotGrabScheduled.compareAndSet(false, true)) {
            return;
        }
        var fileName = pendingScreenshot;
        pendingScreenshot = null;
        var client = Minecraft.getInstance();
        Screenshot.grab(client.gameDirectory, fileName, client.getMainRenderTarget(), 1, message -> {
            screenshotGrabScheduled.set(false);
            LOG.info(fileName + ": " + message.getString());
            copyToArtifacts(fileName, client.gameDirectory);
            if (afterScreenshot != null) {
                var next = afterScreenshot;
                afterScreenshot = null;
                client.execute(next);
            }
        });
    }

    public static void hook() {
        ClientTickEvents.END_CLIENT_TICK.register(new ClientTickEvents.EndTick() {
            private int ticks;
            private Phase phase = Phase.WAIT_BOOT;
            private int phaseTicks;
            private boolean captureRequested;

            @Override
            public void onEndTick(Minecraft client) {
                ticks++;
                client.options.hideGui = false;
                client.options.pauseOnLostFocus = false;
                switch (phase) {
                    case WAIT_BOOT -> {
                        if (ticks < 40 || client.level != null) {
                            if (client.level != null && client.player != null) {
                                phase = Phase.SETUP;
                                phaseTicks = 0;
                            }
                            return;
                        }
                        deleteSave(client);
                        LOG.info("Creating issue 6 verification world");
                        createFlatWorld(client);
                        phase = Phase.WAIT_WORLD;
                        phaseTicks = 0;
                    }
                    case WAIT_WORLD -> {
                        if (client.player != null && client.level != null) {
                            phase = Phase.SETUP;
                            phaseTicks = 0;
                        } else if (++phaseTicks > 12000) {
                            fail(client, "timed out waiting for world");
                        }
                    }
                    case SETUP -> {
                        if (phaseTicks++ < 20) {
                            return;
                        }
                        var player = client.player;
                        if (player == null) {
                            return;
                        }
                        // Blend 0 keeps neighbouring plains from sneaking into a small fill.
                        Biomes.feature().setBiomeBlend(0);
                        player.connection.sendCommand("time set day");
                        player.connection.sendCommand("weather clear");
                        // Superflat surface is around Y -60. Y 80 is open sky and the player falls.
                        player.connection.sendCommand("tp @s 8 -60 8");
                        phase = Phase.WAIT_TP;
                        phaseTicks = 0;
                    }
                    case WAIT_TP -> {
                        if (phaseTicks++ < 40) {
                            return;
                        }
                        client.player.connection.sendCommand("fillbiome 2 -64 2 14 16 14 minecraft:desert");
                        if (!AmbientDebugOverlay.isEnabled()) {
                            AmbientDebugKeys.pressToggle();
                        }
                        phase = Phase.WAIT_DESERT_DAY;
                        phaseTicks = 0;
                    }
                    case WAIT_DESERT_DAY -> {
                        if (desertDaySettled() || phaseTicks++ > 400) {
                            LOG.info("Desert day settled: " + describePlayback());
                            client.player.connection.sendCommand("time set night");
                            phase = Phase.WAIT_CROSSFADE;
                            phaseTicks = 0;
                        }
                    }
                    case WAIT_CROSSFADE -> {
                        if (crossfadeVisible() || phaseTicks++ > 160) {
                            LOG.info("Crossfade: " + describePlayback());
                            prepareWorld(client);
                            phase = Phase.SHOT_SORTED;
                            phaseTicks = 0;
                            captureRequested = false;
                        }
                    }
                    case SHOT_SORTED -> shot(client, "debug-overlay-active-sorted.png", () -> {
                        client.player.connection.sendCommand("time set day");
                        phase = Phase.WAIT_DAY_AGAIN;
                        phaseTicks = 0;
                    });
                    case WAIT_DAY_AGAIN -> {
                        if (desertDaySettled() || phaseTicks++ > 400) {
                            LOG.info("Day restored before debug tab: " + describePlayback());
                            client.setScreen(new AmbienceGardenSettingsScreen(null, AmbienceGardenSettingsScreen.Tab.DEBUG));
                            phase = Phase.SHOT_DEBUG_OFF;
                            phaseTicks = 0;
                            captureRequested = false;
                        }
                    }
                    case SHOT_DEBUG_OFF -> shot(client, "debug-tab-override-off.png", () -> {
                        phase = Phase.SHOT_DEBUG_DESERT;
                        phaseTicks = 0;
                        captureRequested = false;
                    });
                    case SHOT_DEBUG_DESERT -> {
                        if (client.screen instanceof AmbienceGardenSettingsScreen settings) {
                            settings.scrollTo("biome.desert.day");
                        }
                        shot(client, "debug-tab-desert-live.png", () -> {
                            client.setScreen(null);
                            SoundOverride.setActive(true);
                            SoundOverride.setVolume("biome.desert.day", 0.0f);
                            SoundOverride.setVolume("biome.badlands.day", 0.95f);
                            phase = Phase.WAIT_OVERRIDE;
                            phaseTicks = 0;
                        });
                    }
                    case WAIT_OVERRIDE -> {
                        if (phaseTicks++ < 20) {
                            return;
                        }
                        LOG.info("Override playback: " + describePlayback());
                        client.setScreen(new AmbienceGardenSettingsScreen(null, AmbienceGardenSettingsScreen.Tab.DEBUG));
                        phase = Phase.SHOT_DEBUG_ON;
                        phaseTicks = 0;
                        captureRequested = false;
                    }
                    case SHOT_DEBUG_ON -> shot(client, "debug-tab-override-on.png", () -> {
                        if (client.screen instanceof AmbienceGardenSettingsScreen settings) {
                            boolean focused = settings.focusVolumeSlider("biome.badlands.day");
                            LOG.info("Focused badlands day slider: " + focused);
                        }
                        phase = Phase.SHOT_FOCUS;
                        phaseTicks = 0;
                        captureRequested = false;
                    });
                    case SHOT_FOCUS -> shot(client, "debug-tab-slider-focused.png", () -> {
                        client.setScreen(null);
                        if (!AmbientDebugOverlay.isEnabled()) {
                            AmbientDebugKeys.pressToggle();
                        }
                        phase = Phase.SHOT_OVERRIDE_OVERLAY;
                        phaseTicks = 0;
                        captureRequested = false;
                    });
                    case SHOT_OVERRIDE_OVERLAY -> shot(client, "debug-overlay-override-on.png", () -> {
                        if (AmbientDebugOverlay.isEnabled()) {
                            AmbientDebugKeys.pressToggle();
                        }
                        phase = Phase.SHOT_TAG;
                        phaseTicks = 0;
                        captureRequested = false;
                    });
                    case SHOT_TAG -> shot(client, "debug-override-tag-gameplay.png", () -> {
                        SoundOverride.setActive(false);
                        if (!AmbientDebugOverlay.isEnabled()) {
                            AmbientDebugKeys.pressToggle();
                        }
                        phase = Phase.WAIT_RESTORED;
                        phaseTicks = 0;
                    });
                    case WAIT_RESTORED -> {
                        if ((desertDaySettled() && !overrideStillAudible()) || phaseTicks++ > 400) {
                            LOG.info("After override off: " + describePlayback());
                            prepareWorld(client);
                            phase = Phase.SHOT_RESTORED;
                            phaseTicks = 0;
                            captureRequested = false;
                        }
                    }
                    case SHOT_RESTORED -> shot(client, "debug-overlay-restored.png", () -> {
                        LOG.info("Issue 6 captures finished");
                        client.stop();
                    });
                }
            }

            private void shot(Minecraft client, String file, Runnable next) {
                prepareWorld(client);
                if (phaseTicks > 200) {
                    fail(client, "screenshot timed out for " + file);
                    return;
                }
                if (phaseTicks++ < 8) {
                    return;
                }
                if (!captureRequested) {
                    captureRequested = true;
                    requestScreenshot(file, next);
                }
            }

            private void fail(Minecraft client, String reason) {
                LOG.warn(reason + " playback=" + describePlayback());
                client.stop();
            }
        });
    }

    private static boolean desertDaySettled() {
        boolean day = false;
        boolean night = false;
        for (PlaybackListing.Entry entry : SoundOverride.playbackLines()) {
            if (entry.id().endsWith("biome.desert.day") && entry.volume() > 0.40f && !entry.fadingIn()) {
                day = true;
            }
            if (entry.id().endsWith("biome.desert.night") && entry.volume() > 0.05f) {
                night = true;
            }
            if (entry.id().contains("plains") && entry.volume() > 0.05f) {
                return false;
            }
        }
        return day && !night;
    }

    private static boolean crossfadeVisible() {
        boolean day = false;
        boolean nightFading = false;
        for (PlaybackListing.Entry entry : SoundOverride.playbackLines()) {
            if (entry.id().endsWith("biome.desert.day") && entry.volume() > 0.10f) {
                day = true;
            }
            if (entry.id().endsWith("biome.desert.night") && entry.fadingIn() && entry.volume() > 0.0f) {
                nightFading = true;
            }
        }
        return day && nightFading;
    }

    private static boolean overrideStillAudible() {
        for (PlaybackListing.Entry entry : SoundOverride.playbackLines()) {
            if (entry.id().endsWith("biome.badlands.day") && entry.volume() > 0.05f) {
                return true;
            }
        }
        return SoundOverride.isActive();
    }

    private static String describePlayback() {
        return SoundOverride.playbackLines().stream()
            .map(entry -> entry.id() + "=" + String.format("%.2f", entry.volume()) + (entry.fadingIn() ? " fading" : ""))
            .collect(Collectors.joining(", "));
    }

    private static void prepareWorld(Minecraft client) {
        client.options.hideGui = false;
        client.options.pauseOnLostFocus = false;
    }

    private static void requestScreenshot(String fileName, Runnable next) {
        afterScreenshot = next;
        pendingScreenshot = fileName;
        captureDelayTicks = 5;
        screenshotGrabScheduled.set(false);
    }

    private static void createFlatWorld(Minecraft client) {
        var rules = new GameRules(WorldDataConfiguration.DEFAULT.enabledFeatures());
        var settings = new LevelSettings(
            WORLD_NAME,
            GameType.CREATIVE,
            false,
            Difficulty.NORMAL,
            true,
            rules,
            WorldDataConfiguration.DEFAULT
        );
        client.createWorldOpenFlows().createFreshLevel(
            WORLD_NAME,
            settings,
            WorldOptions.defaultWithRandomSeed(),
            WorldPresets::createFlatWorldDimensions,
            null
        );
    }

    private static void deleteSave(Minecraft client) {
        File saves = new File(client.gameDirectory, "saves/" + WORLD_NAME);
        if (saves.isDirectory()) {
            deleteRecursive(saves);
        }
    }

    private static void deleteRecursive(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursive(child);
            }
        }
        file.delete();
    }

    private static void copyToArtifacts(String fileName, File gameDir) {
        File shot = new File(new File(gameDir, "screenshots"), fileName);
        if (!shot.isFile()) {
            return;
        }
        copyShot(shot, Path.of("/opt/cursor/artifacts/screenshots", fileName));
        copyShot(shot, gameDir.toPath().resolve("../docs/screenshots").normalize().resolve(fileName));
    }

    private static void copyShot(File shot, Path target) {
        try {
            target.getParent().toFile().mkdirs();
            java.nio.file.Files.copy(shot.toPath(), target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            LOG.warn("Could not copy screenshot to " + target + ": " + e.getMessage());
        }
    }

    private enum Phase {
        WAIT_BOOT,
        WAIT_WORLD,
        SETUP,
        WAIT_TP,
        WAIT_DESERT_DAY,
        WAIT_CROSSFADE,
        SHOT_SORTED,
        WAIT_DAY_AGAIN,
        SHOT_DEBUG_OFF,
        SHOT_DEBUG_DESERT,
        WAIT_OVERRIDE,
        SHOT_DEBUG_ON,
        SHOT_FOCUS,
        SHOT_OVERRIDE_OVERLAY,
        SHOT_TAG,
        WAIT_RESTORED,
        SHOT_RESTORED
    }
}
