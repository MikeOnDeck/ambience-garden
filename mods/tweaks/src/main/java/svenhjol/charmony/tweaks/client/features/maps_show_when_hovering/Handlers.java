package svenhjol.charmony.tweaks.client.features.maps_show_when_hovering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import svenhjol.charmony.core.base.Setup;

import java.util.List;

public class Handlers extends Setup<MapsShowWhenHovering> {
    public static final ResourceLocation MAP_BACKGROUND = ResourceLocation.parse("textures/map/map_background.png");
    private final MapRenderState mapRenderState = new MapRenderState();

    public Handlers(MapsShowWhenHovering feature) {
        super(feature);
    }

    /**
     * Show a rendered map when hovering over a filled map item.
     */
    public void renderMapTooltip(GuiGraphics guiGraphics, ItemStack stack, List<ClientTooltipComponent> lines, int tx, int ty) {
        if (stack != null && stack.getItem() == Items.FILLED_MAP) {
            var minecraft = Minecraft.getInstance();
            var level = minecraft.level;
            if (level == null) return;

            if (!stack.has(DataComponents.MAP_ID)) return;
            var mapId = stack.get(DataComponents.MAP_ID);

            var data = MapItem.getSavedData(mapId, level);
            if (data == null) return;

            var x = tx;
            var y = ty - 72;
            var w = 64;
            var right = x + w;

            if (right > minecraft.getWindow().getGuiScaledWidth()) {
                x = minecraft.getWindow().getGuiScaledWidth() - w;
            }

            if (y < 0) {
                y = ty + lines.size() * 10 + 8;
            }

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MAP_BACKGROUND,
                x - 3, y - 3, 0, 0, 64, 64, 64, 64);

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(x, y);
            guiGraphics.pose().scale(0.455f, 0.455f);

            minecraft.getMapRenderer().extractRenderState(mapId, data, this.mapRenderState);
            guiGraphics.submitMapRenderState(this.mapRenderState);
            guiGraphics.pose().popMatrix();
        }
    }
}
