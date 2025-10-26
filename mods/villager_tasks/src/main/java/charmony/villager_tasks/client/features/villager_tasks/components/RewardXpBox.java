package charmony.villager_tasks.client.features.villager_tasks.components;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public class RewardXpBox extends AspectBox {
    private final int count;

    public RewardXpBox(int count) {
        this.count = count;
    }

    @Override
    public Optional<ItemStack> itemStack() {
        return Optional.of(new ItemStack(Items.EXPERIENCE_BOTTLE));
    }

    @Override
    public String text() {
        return "" + count;
    }

    @Override
    public Color fillColor() {
        return new Color(0x40b0b0);
    }

    @Override
    public int fillAlpha() {
        return super.fillAlpha();
    }

    @Override
    protected void modifyItemStackTooltip(List<Component> tooltips) {
        tooltips.clear();
        tooltips.add(Resources.YOU_RECEIVE);
        tooltips.add(Component.translatable("gui.charmony.villager_tasks.experience_levels", count));
    }
}
