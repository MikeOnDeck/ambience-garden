package charmony.villager_tasks.client.features.villager_tasks.tooltips;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;

public class MapTooltip extends BaseTooltip {
    public static final ResourceLocation MAP_BACKGROUND = ResourceLocation.parse("textures/map/map_background.png");
    private final MapRenderState mapRenderState = new MapRenderState();
    private final ItemStack stack;

    public MapTooltip(ItemStack map) {
        this.stack = map;
    }

    @Override
    public void renderImage(Font font, int x, int y, int xx, int yy, GuiGraphics guiGraphics) {
        var calcWidth = 0;
        var calcHeight = 6;
        var xy = Pair.of(0, 0);
        startScaling(guiGraphics, x, y);

        var minecraft = Minecraft.getInstance();
        var level = minecraft.level;
        var player = minecraft.player;
        if (level == null || player == null) return;

        if (!stack.has(DataComponents.MAP_ID)) return;
        var mapId = stack.get(DataComponents.MAP_ID);

        var savedData = MapItem.getSavedData(mapId, level);
        if (savedData == null) return;
        savedData.unlimitedTracking = true;
//        savedData.addDecoration(MapDecorationTypes.PLAYER, level, player.getPlainTextName(), player.getX(), player.getZ(), player.getYRot(), null);
//        savedData.setDirty();

        var w = 64;
        var right = x + w;

        if (right > minecraft.getWindow().getGuiScaledWidth()) {
            x = minecraft.getWindow().getGuiScaledWidth() - w;
        }

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MAP_BACKGROUND,
            x - 3, y - 3, 0, 0, 64, 64, 64, 64);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x, y);
        guiGraphics.pose().scale(0.455f, 0.455f);

        minecraft.getMapRenderer().extractRenderState(mapId, savedData, this.mapRenderState);
//        var decorations = this.mapRenderState.decorations;
        this.mapRenderState.decorations.forEach(d -> d.renderOnFrame = true);
//        this.mapRenderState.decorations = decorations;

        guiGraphics.submitMapRenderState(this.mapRenderState);
        guiGraphics.pose().popMatrix();

        calcHeight -= 5; // Remove last padding
        recalculateDimensions(calcWidth, calcHeight);
        stopScaling(guiGraphics);
    }
}
