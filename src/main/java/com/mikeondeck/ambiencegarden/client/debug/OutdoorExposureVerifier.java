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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Captures debug-overlay screenshots for outdoor-exposure roof cases. Set
 * {@code AMBIENCE_GARDEN_VERIFY_OUTDOOR_EXPOSURE=true} and run the client once.
 */
public final class OutdoorExposureVerifier {
    private static final Log LOG = new Log(AmbientSoundsMod.ID, "OutdoorVerify");
    private static final String WORLD_NAME = "ambience_garden_outdoor_exposure";
    private static volatile String pendingScreenshot;
    private static volatile Runnable afterScreenshot;
    private static int captureDelayTicks;
    private static final AtomicBoolean screenshotGrabScheduled = new AtomicBoolean();

    private OutdoorExposureVerifier() {}

    public static boolean enabled() {
        String raw = System.getProperty("ambience_garden.verifyOutdoorExposure");
        if (raw == null || raw.isBlank()) {
            raw = System.getenv("AMBIENCE_GARDEN_VERIFY_OUTDOOR_EXPOSURE");
        }
        return raw != null && !raw.isBlank() && !"false".equalsIgnoreCase(raw);
    }

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

    private static void requestScreenshot(String fileName, Runnable next) {
        afterScreenshot = next;
        pendingScreenshot = fileName;
        captureDelayTicks = 8;
        screenshotGrabScheduled.set(false);
    }

    public static void hook() {
        var cases = List.of(
            new RoofCase("issue-8-stair-roof.png", List.of(
                "fill 97 86 97 103 86 103 minecraft:oak_stairs[half=top]"
            )),
            new RoofCase("issue-8-bottom-slab-roof.png", List.of(
                "fill 97 86 97 103 86 103 minecraft:oak_slab[type=bottom]"
            )),
            new RoofCase("issue-8-top-slab-roof.png", List.of(
                "fill 97 86 97 103 86 103 minecraft:oak_slab[type=top]"
            )),
            new RoofCase("issue-8-stone-roof.png", List.of(
                "fill 97 86 97 103 86 103 minecraft:stone"
            )),
            new RoofCase("issue-8-open-sky.png", List.of()),
            new RoofCase("issue-8-glass-roof.png", List.of(
                "fill 97 86 97 103 86 103 minecraft:glass"
            )),
            new RoofCase("issue-8-oak-leaves.png", List.of(
                "fill 97 86 97 103 86 103 minecraft:oak_leaves[persistent=true,distance=1]"
            ))
        );

        ClientTickEvents.END_CLIENT_TICK.register(new ClientTickEvents.EndTick() {
            private int ticks;
            private Phase phase = Phase.WAIT_BOOT;
            private int phaseTicks;
            private int caseIndex;
            private boolean shotRequested;

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
                        if (client.level == null && client.player == null) {
                            createFlatWorld(client);
                            phase = Phase.WAIT_WORLD;
                            phaseTicks = 0;
                        } else if (client.player != null && client.level != null) {
                            phase = Phase.SETUP_BASE;
                            phaseTicks = 0;
                        }
                    }
                    case WAIT_WORLD -> {
                        if (client.player != null && client.level != null) {
                            phase = Phase.SETUP_BASE;
                            phaseTicks = 0;
                        } else if (++phaseTicks > 12000) {
                            fail(client, "timed out waiting for world");
                        }
                    }
                    case SETUP_BASE -> {
                        if (phaseTicks++ < 20) {
                            return;
                        }
                        var player = client.player;
                        if (player == null) {
                            return;
                        }
                        var connection = player.connection;
                        connection.sendCommand("time set day");
                        connection.sendCommand("weather clear");
                        connection.sendCommand("fill 96 78 96 104 78 104 minecraft:stone_bricks");
                        connection.sendCommand("fill 95 79 95 105 90 105 air");
                        connection.sendCommand("tp @s 100.5 80 100.5");
                        connection.sendCommand("effect give @s night_vision 999 0 true");
                        phase = Phase.START_CASE;
                        phaseTicks = 0;
                        caseIndex = 0;
                    }
                    case START_CASE -> {
                        if (phaseTicks++ < 15) {
                            return;
                        }
                        if (caseIndex >= cases.size()) {
                            LOG.info("All outdoor exposure screenshots captured");
                            client.stop();
                            return;
                        }
                        var player = client.player;
                        if (player == null) {
                            return;
                        }
                        var connection = player.connection;
                        connection.sendCommand("fill 97 86 97 103 86 103 air");
                        for (String command : cases.get(caseIndex).commands) {
                            connection.sendCommand(command);
                        }
                        connection.sendCommand("tp @s 100.5 80 100.5");
                        phase = Phase.WAIT_CASE;
                        phaseTicks = 0;
                        shotRequested = false;
                    }
                    case WAIT_CASE -> {
                        if (phaseTicks++ < 40) {
                            return;
                        }
                        if (client.screen != null) {
                            client.setScreen(null);
                        }
                        if (!AmbientDebugOverlay.isEnabled()) {
                            AmbientDebugKeys.pressToggle();
                        }
                        if (!AmbientDebugOverlay.isEnabled()) {
                            if (phaseTicks > 120) {
                                fail(client, "overlay not enabled for " + cases.get(caseIndex).fileName);
                            }
                            return;
                        }
                        if (phaseTicks < 55) {
                            return;
                        }
                        if (!shotRequested) {
                            shotRequested = true;
                            String file = cases.get(caseIndex).fileName;
                            int nextIndex = caseIndex + 1;
                            requestScreenshot(file, () -> {
                                caseIndex = nextIndex;
                                phase = Phase.START_CASE;
                                phaseTicks = 0;
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

    private record RoofCase(String fileName, List<String> commands) {}

    private enum Phase {
        WAIT_BOOT,
        WAIT_WORLD,
        SETUP_BASE,
        START_CASE,
        WAIT_CASE
    }
}
