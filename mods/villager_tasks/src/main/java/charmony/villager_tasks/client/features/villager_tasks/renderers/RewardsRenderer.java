package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class RewardsRenderer extends BaseRenderer {
    public RewardsRenderer(Task task) {
        super(task);
    }

    public Pair<Integer, Integer> renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        var calcHeight = 0;
        var calcWidth = 0;

        guiGraphics.drawString(font, Resources.REWARD_ASPECT, x, y + calcHeight, new Color(0xffffff).getArgbColor(), false);
        calcHeight += 10;

        var items = task.rewards.items;
        var rows = items.size();

        // Items
        for (var i = 0; i < Math.min(3, items.size()); i++) {
            var item = items.get(i);
            calcWidth = renderItemTooltip(guiGraphics, item.stack(), Component.literal("" + item.total()), x, y + calcHeight + (i * 15));
        }

        // XP
        if (task.rewards.experience > 0) {
            var component = Component.translatable("gui.charmony.villager_tasks.experience_levels", task.rewards.experience);
            calcWidth = renderItemTooltip(guiGraphics, new ItemStack(Items.EXPERIENCE_BOTTLE), component, x, y + calcHeight + (rows * 15), false);
            rows += 1;
        }

        calcHeight += (rows * 15) + 10;
        return Pair.of(calcWidth, calcHeight);
    }
}
