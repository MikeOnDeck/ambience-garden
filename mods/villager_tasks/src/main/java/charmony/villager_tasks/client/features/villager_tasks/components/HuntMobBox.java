package charmony.villager_tasks.client.features.villager_tasks.components;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.requirements.HuntMob;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public class HuntMobBox extends AspectBox {
    private final HuntMob huntMob;
    private final boolean showProgress;

    public HuntMobBox(HuntMob huntMob, boolean showProgress) {
        this.huntMob = huntMob;
        this.showProgress = showProgress;
    }

    @Override
    protected void modifyItemStackTooltip(List<Component> tooltips) {
        var itemName = tooltips.getFirst().getString();
        tooltips.clear();
        tooltips.add(huntMob.isSatisfied() ? Resources.YOU_HUNT : Resources.YOU_HUNTED);
        tooltips.add(Component.literal(itemName + ": " + huntMob.total()));
    }

    @Override
    public String text() {
        if (!showProgress) {
            return "" + huntMob.total();
        }

        var completed = huntMob.total() - huntMob.remaining();
        return completed + "/" + huntMob.total();
    }

    @Override
    public Optional<ResourceLocation> mob() {
        return super.mob();
    }

    @Override
    public Color fillColor() {
        var satisfied = huntMob.isSatisfied();
        var none = huntMob.remaining() == huntMob.total();
        var some = huntMob.remaining() < huntMob.total() && !satisfied;

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
