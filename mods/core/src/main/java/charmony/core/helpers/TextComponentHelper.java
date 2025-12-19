package charmony.core.helpers;

import com.google.common.base.Function;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public final class TextComponentHelper {
    public static List<Component> toComponents(String string, int lineLength) {
        List<Component> out = new ArrayList<>();
        int lineSize = 0;
        StringBuilder buffer = new StringBuilder();
        Function<String, Component> convertText =
            s -> Component.literal(s != null ? s.trim().replace("\n", " ") : "");

        for (int i = 0; i < string.length(); i++) {
            var currentChar = string.charAt(i);
            buffer.append(currentChar);
            if (lineSize++ >= lineLength) {
                if (currentChar == ' ' && string.length() - i > 4) {
                    out.add(convertText.apply(buffer.toString()));
                    buffer = new StringBuilder();
                    lineSize = 0;
                }
            }
        }

        out.add(convertText.apply(buffer.toString()));
        return out;
    }

    /**
     * Get formatted text component of the given blockpos.
     */
    public static Component positionAsText(BlockPos pos) {
        return Component.translatable("gui.charmony.labels.xyz", pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Get formatted text component of the given dimension.
     */
    public static Component dimensionAsText(ResourceKey<Level> dimension) {
        return Component.translatable(dimensionLocaleKey(dimension));
    }

    /**
     * Get a locale key for a dimension.
     */
    public static String dimensionLocaleKey(ResourceKey<Level> dimension) {
        var location = dimension.identifier();
        var namespace = location.getNamespace();
        var path = location.getPath();
        return "dimension." + namespace + "." + path;
    }

    /**
     * Wrap string at a sensible line length and converts into a list of components.
     */
    public static List<Component> wrap(String str) {
        List<Component> out = new ArrayList<>();
        // Split by newlines to preserve manual formatting
        for (String line : str.split("\n")) {
            StringBuilder lineBuffer = new StringBuilder();
            int currentLineLength = 0;

            // Split by space to find wrap points
            String[] words = line.split(" ", -1);
            for (String word : words) {
                // If adding this word exceeds 30 chars, and we aren't at the start of a line
                if (currentLineLength + word.length() > 30 && currentLineLength > 0) {
                    out.add(Component.literal(lineBuffer.toString()));
                    lineBuffer = new StringBuilder();
                    currentLineLength = 0;
                }

                if (currentLineLength > 0) {
                    lineBuffer.append(" ");
                    currentLineLength++;
                }

                lineBuffer.append(word);
                currentLineLength += word.length();
            }
            if (!lineBuffer.isEmpty() || line.isEmpty()) {
                out.add(Component.literal(lineBuffer.toString()));
            }
        }
        return out;
    }

    public static void drawCenteredString(GuiGraphics guiGraphics, Font font, Component component, int x, int y, int color) {
        var formattedCharSequence = component.getVisualOrderText();
        guiGraphics.drawString(font, formattedCharSequence, x - font.width(formattedCharSequence) / 2, y, color, false);
    }

    public static void drawCenteredString(GuiGraphics guiGraphics, Font font, Component component, int x, int y, int color, boolean dropShadow) {
        var formattedCharSequence = component.getVisualOrderText();
        guiGraphics.drawString(font, formattedCharSequence, x - font.width(formattedCharSequence) / 2, y, color, dropShadow);
    }
}
