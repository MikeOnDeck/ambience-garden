package charmony.villager_tasks.client.features.villager_tasks.components;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.requirements.CollectItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class CollectItemBox extends AspectBox {
    private final CollectItem collectItem;
    private final boolean showProgress;

    public CollectItemBox(CollectItem collectItem) {
        this(collectItem, false);
    }

    public CollectItemBox(CollectItem collectItem, boolean showProgress) {
        this.collectItem = collectItem;
        this.showProgress = showProgress;
    }

    @Override
    protected void modifyItemStackTooltip(List<Component> tooltips) {
        var itemName = tooltips.getFirst().getString();
        tooltips.clear();
        tooltips.add(collectItem.isSatisfied() ? Resources.YOU_COLLECTED : Resources.YOU_COLLECT);
        tooltips.add(Component.literal(itemName + ": " + collectItem.total()));
    }

    @Override
    public String text() {
        if (!showProgress) {
            return "" + collectItem.total();
        }

        var completed = collectItem.total() - collectItem.remaining();
        return completed + "/" + collectItem.total();
    }

    @Override
    public Optional<ItemStack> itemStack() {
        return Optional.of(collectItem.stack());
    }

    @Override
    public Color fillColor() {
        var satisfied = collectItem.isSatisfied();
        var none = collectItem.remaining() == collectItem.total();
        var some = collectItem.remaining() < collectItem.total() && !satisfied;

        if (satisfied) {
            return getCompleteColor();
        } else if (some) {
            return getProgressColor();
        } else if (none) {
            return getMissingColor();
        } else {
            return DEFAULT_FILL_COLOR;
        }
    }

    public Color getMissingColor() {
        return new Color(0xb00000);
    }

    public Color getProgressColor() {
        return new Color(0xc09000);
    }

    public Color getCompleteColor() {
        return new Color(0x00a020);
    }

    @Override
    public int fillAlpha() {
        return 120;
    }
}
