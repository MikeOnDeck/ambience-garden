package com.mikeondeck.ambiencegarden.client.debug;

import com.mikeondeck.ambiencegarden.AmbientSoundsMod;
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

/**
 * Optional headless screenshot helper. Set {@code AMBIENCE_GARDEN_VERIFY_DEBUG_OVERLAY} to
 * {@code outdoor} or {@code indoor} and run the client once per capture (separate runs).
 */
public final class AmbientDebugOverlayVerifier {
    private static final Log LOG = new Log(AmbientSoundsMod.ID, "DebugVerify");
    private static final String WORLD_NAME = "ambience_garden_debug_overlay";
    private static volatile String pendingScreenshot;
    private static volatile Runnable afterScreenshot;
    private static int captureDelayTicks;
    private static final AtomicBoolean screenshotGrabScheduled = new AtomicBoolean();

    private AmbientDebugOverlayVerifier() {}

    public enum Mode {
        OFF,
        OUTDOOR,
        INDOOR,
        ALL
    }

    public static Mode mode() {
        String raw = System.getProperty("ambience_garden.verifyDebugOverlay");
        if (raw == null || raw.isBlank()) {
            raw = System.getenv("AMBIENCE_GARDEN_VERIFY_DEBUG_OVERLAY");
        }
        if (raw == null || raw.isBlank() || "false".equalsIgnoreCase(raw)) {
            return Mode.OFF;
        }
        if ("outdoor".equalsIgnoreCase(raw)) {
            return Mode.OUTDOOR;
        }
        if ("indoor".equalsIgnoreCase(raw)) {
            return Mode.INDOOR;
        }
        return Mode.ALL;
    }

    public static boolean enabled() {
        return mode() != Mode.OFF;
    }

    /**
     * Same capture as the screenshot key: {@code Screenshot.grab} of the main render target
     * during the client tick, which still holds the previous frame after {@code GuiRenderer}
     * has drawn the HUD into it and before the next frame clears that target.
     */
    public static void endClientTick() {
        if (!enabled() || pendingScreenshot == null) {
            return;
        }
        if (captureDelayTicks > 0) {
            captureDelayTicks--;
            return;
        }
        if (!AmbientDebugOverlay.isEnabled()) {
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

    private static void copyToArtifacts(String fileName, File gameDir) {
        File shot = new File(new File(gameDir, "screenshots"), fileName);
        if (!shot.isFile()) {
            return;
        }
        copyShot(shot, Path.of("/opt/cursor/artifacts/screenshots", fileName));
        // run/ is the client game directory; docs/ sits next to it in the repo.
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

    private static void prepareForCapture(Minecraft client) {
        client.options.hideGui = false;
        client.options.pauseOnLostFocus = false;
        if (client.screen != null) {
            client.setScreen(null);
        }
    }

    private static void requestScreenshot(String fileName, Runnable next) {
        afterScreenshot = next;
        pendingScreenshot = fileName;
        captureDelayTicks = 5;
        screenshotGrabScheduled.set(false);
    }

    public static void hook() {
        var captureMode = mode();
        ClientTickEvents.END_CLIENT_TICK.register(new ClientTickEvents.EndTick() {
            // world setup only
            private int ticks;
            private Phase phase = Phase.WAIT_BOOT;
            private int phaseTicks;
            private boolean captureRequested;
            private boolean overlayArmed;

            @Override
            public void onEndTick(Minecraft client) {
                ticks++;
                client.options.hideGui = false;
                client.options.pauseOnLostFocus = false;
                switch (phase) {
                    case WAIT_BOOT -> {
                        if (ticks < 40) {
                            return;
                        }
                        if (client.level != null && client.player != null) {
                            phase = Phase.SETUP;
                            phaseTicks = 0;
                            return;
                        }
                        if (captureMode == Mode.INDOOR) {
                            LOG.info("Opening verification world " + WORLD_NAME);
                            client.createWorldOpenFlows().openWorld(WORLD_NAME, () -> {});
                            phase = Phase.WAIT_WORLD;
                            phaseTicks = 0;
                        } else if (client.level == null && client.player == null) {
                            createFlatWorld(client);
                            phase = Phase.WAIT_WORLD;
                            phaseTicks = 0;
                        }
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
                        if (captureMode == Mode.INDOOR) {
                            runIndoorCommands(client);
                            phase = Phase.WAIT_SETUP;
                        } else {
                            runOutdoorTeleport(client);
                            phase = Phase.WAIT_AFTER_TP;
                        }
                        phaseTicks = 0;
                    }
                    case WAIT_AFTER_TP -> {
                        if (phaseTicks++ < 40) {
                            return;
                        }
                        runOutdoorBiomeCommands(client);
                        phase = Phase.WAIT_SETUP;
                        phaseTicks = 0;
                    }
                    case WAIT_SETUP -> {
                        if (phaseTicks++ < 60) {
                            return;
                        }
                        prepareForCapture(client);
                        if (!AmbientDebugOverlay.isEnabled()) {
                            LOG.info("Pressing debug overlay key");
                            AmbientDebugKeys.pressToggle();
                        }
                        if (captureMode == Mode.INDOOR) {
                            phase = Phase.SHOT_INDOOR;
                        } else {
                            phase = Phase.SHOT_OUTDOOR;
                        }
                        phaseTicks = 0;
                    }
                    case SHOT_OUTDOOR -> {
                        prepareForCapture(client);
                        if (!AmbientDebugOverlay.isEnabled()) {
                            if (phaseTicks++ > 80) {
                                fail(client, "overlay key was not consumed");
                            }
                            return;
                        }
                        if (!overlayArmed) {
                            overlayArmed = true;
                            phaseTicks = 0;
                        }
                        if (phaseTicks++ < 20) {
                            return;
                        }
                        if (!captureRequested) {
                            captureRequested = true;
                            requestScreenshot("debug-overlay-desert-outdoor.png", () -> {
                                if (captureMode == Mode.ALL) {
                                    runIndoorCommands(client);
                                    phase = Phase.SHOT_INDOOR;
                                    phaseTicks = 0;
                                    captureRequested = false;
                                } else {
                                    LOG.info("Outdoor debug overlay screenshot saved");
                                    client.stop();
                                }
                            });
                        }
                    }
                    case SHOT_INDOOR -> {
                        prepareForCapture(client);
                        if (!AmbientDebugOverlay.isEnabled()) {
                            if (phaseTicks++ > 80) {
                                fail(client, "overlay key was not consumed");
                            }
                            return;
                        }
                        if (!overlayArmed) {
                            overlayArmed = true;
                            phaseTicks = 0;
                        }
                        if (phaseTicks++ < 60) {
                            return;
                        }
                        if (!captureRequested) {
                            captureRequested = true;
                            requestScreenshot("debug-overlay-desert-indoor.png", () -> {
                                LOG.info("Indoor debug overlay screenshot saved");
                                client.stop();
                            });
                        }
                    }
                }
            }

            private void fail(Minecraft client, String reason) {
                LOG.warn(reason);
                client.stop();
            }
        });
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
        LOG.info("Creating flat verification world " + WORLD_NAME);
        client.createWorldOpenFlows().createFreshLevel(
            WORLD_NAME,
            settings,
            WorldOptions.defaultWithRandomSeed(),
            WorldPresets::createFlatWorldDimensions,
            null
        );
    }

    private static void runOutdoorTeleport(Minecraft client) {
        var player = client.player;
        if (player == null) {
            return;
        }
        var connection = player.connection;
        connection.sendCommand("time set day");
        connection.sendCommand("weather clear");
        connection.sendCommand("tp @s 8 80 8");
    }

    private static void runOutdoorBiomeCommands(Minecraft client) {
        var player = client.player;
        if (player == null) {
            return;
        }
        player.connection.sendCommand("fillbiome ~-4 -64 ~-4 ~3 80 ~3 minecraft:desert");
    }

    private static void runIndoorCommands(Minecraft client) {
        var player = client.player;
        if (player == null) {
            return;
        }
        var connection = player.connection;
        connection.sendCommand("tp @s 8 80 8");
        connection.sendCommand("fill ~-5 ~ ~-5 ~5 ~5 ~5 sandstone");
        connection.sendCommand("fill ~-4 ~1 ~-4 ~4 ~4 ~4 air");
        connection.sendCommand("tp @s ~0.5 ~1 ~0.5");
        connection.sendCommand("effect give @s night_vision 30 0 true");
    }

    private enum Phase {
        WAIT_BOOT,
        WAIT_WORLD,
        SETUP,
        WAIT_AFTER_TP,
        WAIT_SETUP,
        SHOT_OUTDOOR,
        SHOT_INDOOR
    }
}
