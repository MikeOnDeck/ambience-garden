package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.villager_tasks.client.features.villager_tasks.component.AspectBoxBuilder;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class RewardsRenderer extends BaseRenderer {
    private @Nullable RegistryAccess registryAccess;

    public RewardsRenderer(Task task) {
        super(task);

        var level = Minecraft.getInstance().level;
        if (level != null) {
            this.registryAccess = level.registryAccess();
        }
    }

    @Override
    public Pair<Integer, Integer> renderTaskHoverTooltip(GuiGraphics guiGraphics, int x, int y) {
        var calcHeight = 0;
        var calcWidth = 0;
        var maxShown = 3;
        var rowHeight = 16;
        var margin = 10;

        var xp = task.rewards.experience;
        var items = task.rewards.items;
        var effects = task.rewards.effects;
        var rows = 0;

        List<Function<Integer, Integer>> renderers = new ArrayList<>();

        for (var item : items) {
            renderers.add(yy -> renderItemInTooltip(guiGraphics, item.stack(), Component.literal("" + item.total()), x, yy));
        }

        if (xp > 0) {
            renderers.add(yy -> {
                var component = Component.translatable("gui.charmony.villager_tasks.experience_levels", task.rewards.experience);
                return renderItemInTooltip(guiGraphics, new ItemStack(Items.EXPERIENCE_BOTTLE), component, x, yy, false);
            });
        }

        if (registryAccess != null) {
            for (var effect : effects) {
                renderers.add(yy -> renderItemInTooltip(guiGraphics, effect.makePotion(registryAccess), effect.name(registryAccess), x, yy, false));
            }
        }

        for (var i = 0; i < Math.min(maxShown, renderers.size()); i++) {
            var renderer = renderers.get(i);
            calcWidth = Math.max(calcWidth, renderer.apply(y + calcHeight + (rows * rowHeight)));
            ++rows;
        }

        if (maxShown < renderers.size()) {
            renderEllipsisInTooltip(guiGraphics, renderers.size() - maxShown, x, y + calcHeight + (rows * rowHeight));
            ++rows;
        }

        calcHeight += (rows * rowHeight) + margin;
        return Pair.of(calcWidth, calcHeight);
    }

    @Override
    public Pair<Integer, Integer> renderPanel(GuiGraphics guiGraphics, int x, int y, int xx, int yy, int maxWidth, int mouseX, int mouseY) {
        var rewards = task.rewards;

        if (rewards.isEmpty()) {
            return Pair.of(xx, yy);
        }

        var boxMargin = 3;

        // Reward XP
        if (rewards.experience > 0) {
            List<Component> tooltips = List.of(
                Resources.YOU_RECEIVE,
                Component.translatable("gui.charmony.villager_tasks.experience_levels", rewards.experience)
            );

            var box = new AspectBoxBuilder()
                .withText(Component.literal("" + rewards.experience))
                .withItemStack(new ItemStack(Items.EXPERIENCE_BOTTLE))
                .withFillColor(new Color(0x40b0b0))
                .withTooltipText(tooltips);

            box.render(guiGraphics, font, x + xx, y + yy, mouseX, mouseY);
            xx += box.width() + boxMargin;
        }

        // Reward items
        if (!rewards.items.isEmpty()) {
            for (var i = 0; i < rewards.items.size(); i++) {
                var item = rewards.items.get(i);
                var stack = item.stack();

                // Reconstruct the stack tooltip
                var itemTooltip = itemTooltip(stack);
                List<Component> tooltip = new ArrayList<>(List.of(
                    Resources.YOU_RECEIVE,
                    nameAndTotal(itemTooltip.getFirst(), item.total())
                ));
                tooltip.addAll(itemTooltip.subList(1, itemTooltip.size()));

                var box = new AspectBoxBuilder()
                    .withText(item.total())
                    .withItemStack(item.stack())
                    .withFillColor(new Color(0x4090c0))
                    .withTooltipText(tooltip);

                box.render(guiGraphics, font, x + xx, y + yy, mouseX, mouseY);
                var width = box.width();
                var height = box.height();

                xx += width + boxMargin;
                if (xx >= maxWidth) {
                    // Move to next row
                    xx = 0;
                    yy += height + boxMargin;
                }
            }
        }

        // Reward effects
        if (registryAccess != null && !rewards.effects.isEmpty()) {
            for (var effect : rewards.effects) {
                var name = effect.name(registryAccess);
                var potion = effect.makePotion(registryAccess);
                var itemTooltip = itemTooltip(potion);
                List<Component> tooltips = new ArrayList<>(List.of(Resources.YOU_RECEIVE));
                tooltips.addAll(itemTooltip.subList(1, itemTooltip.size()));

                var box = new AspectBoxBuilder()
                    .withText(name)
                    .withItemStack(potion)
                    .withFillColor(new Color(0x6040c0))
                    .withTooltipText(tooltips);

                box.render(guiGraphics, font, x + xx, y + yy, mouseX, mouseY);
                var width = box.width();
                var height = box.height();

                xx += width + boxMargin;
                if (xx >= maxWidth) {
                    // Move to next row
                    xx = 0;
                    yy += height + boxMargin;
                }
            }
        }

        return Pair.of(xx, yy);
    }
}
