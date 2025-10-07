package svenhjol.charmony.tweaks.client.features.compasses_show_position;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import svenhjol.charmony.core.base.Setup;

public class Handlers extends Setup<CompassesShowPosition> {
    public Handlers(CompassesShowPosition feature) {
        super(feature);
    }

    @SuppressWarnings("ConstantValue")
    public void hudRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        var minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.player == null) return;
        var player = minecraft.player;

        // Don't show if Hide GUI is enabled.
        if (minecraft.options.hideGui) {
            return;
        }

        if (!feature().alwaysShow()) {
            // Only render hud if player is holding a compass.
            if (player.getOffhandItem().getItem() != Items.COMPASS && player.getMainHandItem().getItem() != Items.COMPASS) {
                return;
            }
        }

        if (feature().onlyShowWhenSneaking() && !player.isCrouching()) {
            return;
        }

        var y = 10;
        var lineHeight = 12;
        var gui = minecraft.gui;
        var font = gui.getFont();
        var direction = player.getDirection();
        var pos = player.blockPosition();
        var alpha = 220 << 24 & 0xff000000;

        String coords;

        if (feature().onlyShowXZ()) {
            coords = I18n.get("gui.charmony.compass.coords_only_xz", pos.getX(), pos.getZ());
        } else {
            coords = I18n.get("gui.charmony.compass.coords", pos.getX(), pos.getY(), pos.getZ());
        }

        var facing = I18n.get("gui.charmony.compass.facing", direction.getName());
        var facingColor = 0xffeedd;
        var coordsColor = 0xaa9988;
        var biomeColor = 0x9accaa;

        if (feature().showFacing()) {
            guiGraphics.drawString(font, facing,16, y, facingColor | alpha);
            y += lineHeight;
        }

        if (feature().showCoords()) {
            guiGraphics.drawString(font, coords,16, y, coordsColor | alpha);
            y += lineHeight;
        }

        if (feature().showBiome()) {
            var biomeRes = player.level().getBiome(pos).unwrap().map(key -> key != null ? key.location() : null, unknown -> null);
            if (biomeRes != null) {
                var biomeName = I18n.get("biome." + biomeRes.getNamespace() + "." + biomeRes.getPath());
                var biomeText = Component.translatable("gui.charmony.compass.biome", biomeName).getString();
                guiGraphics.drawString(font, biomeText,16, y, biomeColor | alpha);
                y += lineHeight;
            }
        }

        for (var item : feature().registers.overlayItems) {
            if (!item.shouldShow(player)) continue;
            guiGraphics.drawString(font, item.text(),16, y, item.color() | alpha);
            y += lineHeight;
        }
    }
}
