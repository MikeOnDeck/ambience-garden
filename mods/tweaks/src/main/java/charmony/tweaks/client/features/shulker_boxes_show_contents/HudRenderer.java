package charmony.tweaks.client.features.shulker_boxes_show_contents;

import charmony.core.client.BaseHudRenderer;
import charmony.core.helpers.TextComponentHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HudRenderer extends BaseHudRenderer {
    private int ix = 0;
    private int iy = 0;

    @Override
    protected boolean withScaling() {
        return true;
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        var minecraft = Minecraft.getInstance();
        var window = minecraft.getWindow();

        if (ticksFade == 0) return;
        ticksBackoff = 2;

        var feature = ShulkerBoxesShowContents.feature();
        var item = feature.handlers.getLastShulkerBoxType();
        var count = feature.handlers.getLastShulkerBoxCount();

        var stack = new ItemStack(item);
        var name = stack.getHoverName().getString() + " x" + count;
        var strLength = name.length();
        var font = minecraft.font;
        var midX = (int)(window.getGuiScaledWidth() / 2.0f);
        var alpha = Math.max(4, Math.min(MAX_FADE_TICKS, ticksFade)) << 24 & 0xff000000;
        var scale = Math.max(0f, Math.min(1.0f, (ticksFade / (float) MAX_FADE_TICKS)));

        var y = 54;
        var lineHeight = 14;

        y += lineHeight;

        var component = Component.literal(name);
        TextComponentHelper.drawCenteredString(guiGraphics, font, component, midX + 18, y, 0xf8f8ff | alpha, false);

        ix = midX - (strLength * 2) - 16;
        iy = y - 5;

        renderScaledGuiItem(guiGraphics, stack, ix, iy, scale, scale);

        doFadeTicks();
    }

    @Override
    protected boolean isValid(Player player) {
        var feature = ShulkerBoxesShowContents.feature();
        return feature.handlers.isLookingAtShulkerBox();
    }

    @Override
    public void scaleItem(ItemStack stack, GuiItemRenderState state, Minecraft minecraft, int width, int height) {
        state.pose().scaleAround(scaleX, scaleY, (float) ix + 7, iy + 8);
        scaleX = scaleY = 1.0f;
    }
}
