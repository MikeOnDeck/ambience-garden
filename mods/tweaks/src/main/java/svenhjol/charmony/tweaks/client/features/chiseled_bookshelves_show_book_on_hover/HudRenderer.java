package svenhjol.charmony.tweaks.client.features.chiseled_bookshelves_show_book_on_hover;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import svenhjol.charmony.core.client.BaseHudRenderer;

public class HudRenderer extends BaseHudRenderer {
    private ItemStack hitStack; // A reference to the item stack currently being pointed at.

    @Override
    protected boolean withScaling() {
        return true;
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (ticksFade == 0) return;
        ticksBackoff = 2;

        var minecraft = Minecraft.getInstance();
        var window = minecraft.getWindow();
        var font = minecraft.font;
        var midX = (int)(window.getGuiScaledWidth() / 2.0f);
        var alpha = Math.max(4, Math.min(MAX_FADE_TICKS, ticksFade)) << 24 & 0xff000000;
        var scale = Math.max(0f, Math.min(1.0f, (ticksFade / (float) MAX_FADE_TICKS)));
        var top = (window.getGuiScaledHeight() / 2) + feature().offsetFromCenter();

        if (hitStack != null && !hitStack.isEmpty()) {
            var lines = hitStack.getTooltipLines(Item.TooltipContext.EMPTY, minecraft.player, TooltipFlag.NORMAL);
            if (!hitStack.isEmpty()) {
                renderScaledGuiItem(guiGraphics, hitStack, midX - 8, top, scale, scale);
                var size = Math.min(4, lines.size()); // Don't render too far on the Y.
                for (int i = 0; i < size; i++) {
                    var component = lines.get(i);
                    guiGraphics.drawCenteredString(font, component, midX, top + 20 + (i * 12), 0xffffff | alpha);
                }
            }
        }

        doFadeTicks();
    }

    @Override
    protected boolean isValid(Player player) {
        var level = player.level();
        var lookedAt = feature().handlers.lookingAtBlock(player);
        var pos = lookedAt.getBlockPos();

        if (level.getBlockEntity(pos) instanceof ChiseledBookShelfBlockEntity blockEntity) {
            var slot = feature().handlers.getHitSlot(lookedAt, level.getBlockState(pos));
            if (slot.isPresent()) {
                var stack = feature().handlers.getItem(blockEntity, slot.getAsInt());
                if (!stack.isEmpty()) {
                    this.hitStack = stack;
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void scaleItem(ItemStack stack, GuiItemRenderState state, Minecraft minecraft, int width, int height) {
        var o = isValid ? 30 : 16;
        state.pose().scaleAround(scaleX, scaleY, (float) width / 2, (float) (height / 2) + o);
        scaleX = scaleY = 1.0f;
    }

    private ChiseledBookshelvesShowBookOnHover feature() {
        return ChiseledBookshelvesShowBookOnHover.feature();
    }
}
