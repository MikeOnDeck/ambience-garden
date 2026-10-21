package com.mikeondeck.ambiencegarden.client.debug;

import com.mikeondeck.ambiencegarden.client.features.biomes.Biomes;
import com.mikeondeck.ambiencegarden.client.features.biomes.Handlers;
import com.mikeondeck.ambiencegarden.client.features.environment.Environment;
import com.mikeondeck.ambiencegarden.client.features.sound.SoundInstance;
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
        if (!enabled) {
            return;
        }

        var client = Minecraft.getInstance();
        var player = client.player;
        if (player == null || client.level == null) {
            return;
        }

        List<String> lines;
        try {
            lines = collectLines(player);
        } catch (RuntimeException e) {
            lines = List.of("Ambience Garden debug", "error: " + e.getClass().getSimpleName());
        }

        var font = client.font;
        int x = 4;
        int y = 4;
        int width = 0;
        for (String line : lines) {
            width = Math.max(width, font.width(line));
        }
        int lineStep = font.lineHeight + 1;
        int height = lines.size() * lineStep + 2;

        // Screen space. A later HUD layer may have left a translated pose.
        graphics.pose().pushMatrix();
        graphics.pose().identity();
        graphics.fill(x - 2, y - 2, x + width + 4, y + height, 0xE0101010);
        // Text must sit in a later stratum or the panel quad can cover it.
        // 1.21.11 drawString drops colours whose alpha byte is 0 (0xFFFFFF).
        graphics.nextStratum();
        int lineY = y;
        for (String line : lines) {
            graphics.drawString(font, line, x, lineY, 0xFFFFFFFF, true);
            lineY += lineStep;
        }
        graphics.pose().popMatrix();
    }

    private static List<String> collectLines(net.minecraft.world.entity.player.Player player) {
        var lines = new ArrayList<String>();
        var level = player.level();
        var biomeKey = level.registryAccess()
            .lookupOrThrow(net.minecraft.core.registries.Registries.BIOME)
            .getKey(level.getBiome(player.blockPosition()).value());
        String biome = biomeKey != null ? biomeKey.getNamespace() + ":" + biomeKey.getPath() : "?";
        Identifier dimension = level.dimension().identifier();

        lines.add("Ambience Garden debug");
        lines.add("dim=" + dimension + " biome=" + biome);
        lines.add("time=" + (WorldHelper.isDay(player) ? "day" : "night")
            + " thunder=" + WorldHelper.isThundering(player));

        OutdoorDiagnostics.analyze(player, SettingsHelper.cullSoundAboveGround())
            .toOverlayLines()
            .forEach(lines::add);

        lines.add("-- biome sounds (enabled=" + Biomes.feature().enabled() + ") --");
        appendBiomeSounds(lines, Biomes.feature().handlers.activeHandler());

        lines.add("-- environment (enabled=" + Environment.feature().enabled() + ") --");
        appendEnvironmentSounds(lines, Environment.feature().handlers.activeHandler());

        return lines;
    }

    private static void appendBiomeSounds(List<String> lines, Handlers.Handler handler) {
        appendSounds(lines, handler == null ? null : handler.getSounds());
    }

    private static void appendEnvironmentSounds(List<String> lines, com.mikeondeck.ambiencegarden.client.features.environment.Handlers.Handler handler) {
        appendSounds(lines, handler == null ? null : handler.getSounds());
    }

    private static void appendSounds(List<String> lines, List<? extends SoundInstance> sounds) {
        if (sounds == null) {
            lines.add("(handler not ready)");
            return;
        }

        for (int i = 0; i < sounds.size(); i++) {
            if (i >= 45) {
                lines.add("... (" + (sounds.size() - i) + " more)");
                break;
            }
            SoundInstance sound = sounds.get(i);
            var line = AmbientSoundDiagnostics.describe(sound);
            char tag = line.playing() ? '*' : (line.eligible() ? '+' : '-');
            lines.add(String.format(
                "%c %s vol=%.2f %s | %s",
                tag,
                shorten(line.id()),
                line.effectiveVolume(),
                line.fadeState(),
                line.reason()
            ));
        }
    }

    private static String shorten(String id) {
        if (id.length() <= 42) {
            return id;
        }
        return "..." + id.substring(id.length() - 39);
    }
}
