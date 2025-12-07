package charmony.villager_tasks.client.features.villager_tasks;

import charmony.core.client.BaseHudRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public class HudRenderer extends BaseHudRenderer {
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (ticksFade == 0) return;

        feature().handlers.getPinnedTaskRenderer().ifPresent(renderer -> {
            renderer.renderHud(guiGraphics, deltaTracker);
        });

        doFadeTicks();
    }

    @Override
    protected boolean isValid(Player player) {
        return feature().handlers.isValidPinnedTask();
    }

    private VillagerTasks feature() {
        return VillagerTasks.feature();
    }
}
