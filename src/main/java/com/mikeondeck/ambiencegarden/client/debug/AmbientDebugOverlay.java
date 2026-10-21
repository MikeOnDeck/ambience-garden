package com.mikeondeck.ambiencegarden.client.debug;

import com.mikeondeck.ambiencegarden.client.sound.PlaybackListing;
import com.mikeondeck.ambiencegarden.client.sound.SoundOverride;
import com.mikeondeck.ambiencegarden.core.helpers.OutdoorDiagnostics;
import com.mikeondeck.ambiencegarden.core.helpers.WorldHelper;
import com.mikeondeck.ambiencegarden.helpers.SettingsHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class AmbientDebugOverlay {
    private static boolean enabled;

    private AmbientDebugOverlay() {}

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static void toggle() {
        enabled = !enabled;
    }

    public static void render(GuiGraphics graphics, DeltaTracker delta) {
        var client = Minecraft.getInstance();
        var player = client.player;
        if (player == null || client.level == null) {
            return;
        }

        boolean override = SoundOverride.isActive();
        if (!enabled && !override) {
            return;
        }

        List<Line> lines = List.of();
        if (enabled) {
            try {
                lines = collectLines(player);
            } catch (RuntimeException e) {
                lines = List.of(new Line("Ambience Garden debug", 0xFFFFFFFF), new Line("error: " + e.getClass().getSimpleName(), 0xFFFFFFFF));
            }
        }

        var font = client.font;
        int x = 4;
        int y = 4;

        // Screen space. A later HUD layer may have left a translated pose.
        // 1.21.11 drawString drops colours whose alpha byte is 0 (0xFFFFFF).
        graphics.pose().pushMatrix();
        graphics.pose().identity();
        if (enabled && !lines.isEmpty()) {
            int width = 0;
            for (Line line : lines) {
                width = Math.max(width, font.width(line.text()));
            }
            int lineStep = font.lineHeight + 1;
            int height = lines.size() * lineStep + 2;
            graphics.fill(x - 2, y - 2, x + width + 4, y + height, 0xE0101010);
            graphics.nextStratum();
            int lineY = y;
            for (Line line : lines) {
                graphics.drawString(font, line.text(), x, lineY, line.argb(), true);
                lineY += lineStep;
            }
        }
        if (override) {
            drawOverrideTag(graphics, client);
        }
        graphics.pose().popMatrix();
    }

    private static void drawOverrideTag(GuiGraphics graphics, Minecraft client) {
        var font = client.font;
        String tag = "OVERRIDE ON";
        int width = font.width(tag);
        int screenW = client.getWindow().getGuiScaledWidth();
        int screenH = client.getWindow().getGuiScaledHeight();
        // Bottom-right stays clear of the top-left overlay and the top-right tutorial toast.
        int x = Math.max(4, screenW - width - 6);
        int y = Math.max(4, screenH - font.lineHeight - 28);
        graphics.fill(x - 3, y - 2, x + width + 3, y + font.lineHeight + 2, 0xE0AA2200);
        graphics.nextStratum();
        graphics.drawString(font, tag, x, y, 0xFFFFFFFF, true);
    }

    private static List<Line> collectLines(net.minecraft.world.entity.player.Player player) {
        var lines = new ArrayList<Line>();
        var level = player.level();
        var biomeKey = level.registryAccess()
            .lookupOrThrow(net.minecraft.core.registries.Registries.BIOME)
            .getKey(level.getBiome(player.blockPosition()).value());
        String biome = biomeKey != null ? biomeKey.getNamespace() + ":" + biomeKey.getPath() : "?";
        Identifier dimension = level.dimension().identifier();

        lines.add(header("Ambience Garden debug"));
        lines.add(header("dim=" + dimension + " biome=" + biome));
        lines.add(header("time=" + (WorldHelper.isDay(player) ? "day" : "night")
            + " thunder=" + WorldHelper.isThundering(player)));

        OutdoorDiagnostics.analyze(player, SettingsHelper.cullSoundAboveGround())
            .toOverlayLines()
            .forEach(text -> lines.add(header(text)));

        lines.add(header("playing:"));
        var playing = SoundOverride.playbackLines();
        if (playing.isEmpty()) {
            lines.add(header("(none)"));
        } else {
            for (PlaybackListing.Entry entry : playing) {
                boolean fading = entry.fadingIn();
                String text = String.format(
                    "%s vol=%.2f%s",
                    entry.id(),
                    entry.volume(),
                    fading ? " fading" : ""
                );
                // Fading-in sounds are dimmer grey; steady playback stays white.
                lines.add(new Line(text, fading ? 0xFF888888 : 0xFFFFFFFF));
            }
        }
        return lines;
    }

    private static Line header(String text) {
        return new Line(text, 0xFFFFFFFF);
    }

    private record Line(String text, int argb) {}
}
