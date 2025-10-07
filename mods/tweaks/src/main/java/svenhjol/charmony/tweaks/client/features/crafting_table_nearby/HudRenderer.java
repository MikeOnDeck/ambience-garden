package svenhjol.charmony.tweaks.client.features.crafting_table_nearby;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.core.client.BaseHudRenderer;

public class HudRenderer extends BaseHudRenderer {
    public ResourceLocation getImage() {
        return ResourceLocation.withDefaultNamespace("textures/block/crafting_table_front.png");
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (ticksFade == 0) return;
        ticksBackoff = 2;

        var minecraft = Minecraft.getInstance();
        var window = minecraft.getWindow();
        var scale = Math.max(0f, Math.min(1.0f, (ticksFade / (float) MAX_FADE_TICKS)));
        int alpha = ARGB.white(scale * 0.75f);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, getImage(), window.getGuiScaledWidth() - 30, window.getGuiScaledHeight() - 30, 0, 0, 16, 16, 16, 16, alpha);

        doFadeTicks();
    }

    @Override
    protected boolean isValid(Player player) {
        var feature = CraftingTableNearby.feature();
        return feature.showCraftingIcon() && feature.handlers.getCraftingTablePos().isPresent();
    }
}
