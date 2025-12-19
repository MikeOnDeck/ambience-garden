package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.core.client.MobSpriteRenderer;
import charmony.villager_tasks.client.features.villager_tasks.component.AspectBoxBuilder;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.LodestoneTracker;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class BattleRenderer extends BaseRenderer {
    private @Nullable RegistryAccess registryAccess;
    private @Nullable Player player;
    private final ItemStack regularCompass = new ItemStack(Items.COMPASS);

    public BattleRenderer(Task task) {
        super(task);

        var level = Minecraft.getInstance().level;
        var player = Minecraft.getInstance().player;

        if (player != null) {
            this.player = player;
        }
        if (level != null) {
            this.registryAccess = level.registryAccess();
        }
    }

    @Override
    public Pair<Integer, Integer> renderTaskHoverTooltip(GuiGraphics guiGraphics, int x, int y) {
        var calcHeight = 0;
        var calcWidth = 0;
        var maxShown = 3;
        var rowHeight = 17;
        var margin = 11;

        var mobs = task.battle.mobs();
        var rows = Math.min(maxShown, mobs.size());
        var showEllipsis = mobs.size() > maxShown;

        if (!mobs.isEmpty()) {
            guiGraphics.drawString(font, Resources.BATTLE_ASPECT, x, y + calcHeight, new Color(0xffffff).getArgbColor(), false);
            calcHeight += margin;

            for (var i = 0; i < rows; i++) {
                var mob = mobs.get(i);
                var spriteRenderer = new MobSpriteRenderer(mob.mob());
                calcWidth = Math.max(calcWidth, renderSpriteInTooltip(guiGraphics, spriteRenderer, Component.literal("" + mob.total()), x, y + calcHeight + (i * rowHeight)));
            }

            if (showEllipsis) {
                renderEllipsisInTooltip(guiGraphics, mobs.size() - maxShown, x, y + calcHeight + (rows * rowHeight));
                rows += 1;
            }

            calcHeight += (rows * rowHeight) + margin;
        }

        return Pair.of(calcWidth, calcHeight);
    }

    @Override
    public Pair<Integer, Integer> renderPanel(GuiGraphics guiGraphics, int x, int y, int xx, int yy, int maxWidth, int mouseX, int mouseY) {
        var battle = task.battle;

        if (!battle.isEmpty()) {
            var boxMargin = 3;

            for (var i = 0; i < battle.mobs().size(); i++) {
                var mob = battle.mobs().get(i);
                var effects = mob.effects();
                var spriteRenderer = new MobSpriteRenderer(mob.mob());
                var allDefeated = battle.allDefeated();

                var trackedCompass = regularCompass.copy();
                var tracker = new LodestoneTracker(mob.globalPos(), true);
                trackedCompass.set(DataComponents.LODESTONE_TRACKER, tracker);

                List<Component> tooltips = new ArrayList<>(List.of(
                    Resources.YOU_MUST_DEFEAT,
                    nameAndTotal(spriteRenderer.getName(), mob.total())
                ));

                var globalPos = mob.globalPos().orElse(null);
                if (player != null && globalPos != null && !allDefeated) {
                    var pos = globalPos.pos();
                    var dist = pos.distManhattan(player.blockPosition());
                    tooltips.add(Component.translatable("gui.charmony.villager_tasks.distance", dist));
                }

                if (registryAccess != null && !effects.isEmpty()) {
                    tooltips.add(Component.empty());
                    tooltips.add(Resources.EFFECTS);
                    for (var effect : effects) {
                        var potion = effect.makePotion(registryAccess);
                        var itemTooltip = itemTooltip(potion);
                        if (itemTooltip.size() > 1) {
                            tooltips.add(itemTooltip.get(1));
                        }
                    }
                }

                var box = new AspectBoxBuilder()
                    .withSpriteRenderer(spriteRenderer)
                    .withGraphicOrder(AspectBoxBuilder.GraphicOrder.SPRITE_FIRST)
                    .withRequirement(mob)
                    .withItemStack(task.isStarted() ? trackedCompass : regularCompass)
                    .withTooltipText(tooltips);

                box.render(guiGraphics, font, x + xx, y + yy, mouseX, mouseY);
                var width = box.width();
                var height = box.height();

                xx += width + boxMargin;
                if (xx + width > maxWidth) {
                    // Move to next row
                    xx = 0;
                    yy += height + boxMargin;
                }
            }
        }

        return Pair.of(xx, yy);
    }

    @Override
    public int renderHud(GuiGraphics guiGraphics, DeltaTracker deltaTracker, int x, int y) {
        if (task.battle.isEmpty() || player == null) {
            return 0;
        }

        var mobs = task.battle.mobs();
        var mob = mobs.stream().filter(m -> !m.isSatisfied()).findFirst().orElse(null);
        if (mob == null) {
            return 0;
        }

        var globalPos = mob.globalPos().orElse(null);
        if (globalPos == null) {
            return 0;
        }

        var trackedCompass = regularCompass.copy();
        var tracker = new LodestoneTracker(mob.globalPos(), true);
        trackedCompass.set(DataComponents.LODESTONE_TRACKER, tracker);

        if (!player.level().dimension().equals(globalPos.dimension())) {
            return 0;
        }

        var normalizedPlayerPos = player.blockPosition().atY(0);
        var minecraft = Minecraft.getInstance();
        var dist = globalPos.pos().distManhattan(normalizedPlayerPos);
        var distanceText = Component.translatable("gui.charmony.villager_tasks.distance", dist);
        var gui = minecraft.gui;
        var font = gui.getFont();
        var color = new Color(0xffffff);

        guiGraphics.renderItem(trackedCompass, x - 2, y - 4);
        guiGraphics.drawString(font, distanceText, x + 16, y, color.getArgbColor());

        return 15;
    }
}
