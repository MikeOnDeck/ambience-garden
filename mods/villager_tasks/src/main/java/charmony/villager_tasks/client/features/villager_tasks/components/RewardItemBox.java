package charmony.villager_tasks.client.features.villager_tasks.components;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.rewards.RewardItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class RewardItemBox extends AspectBox {
    private final RewardItem rewardItem;

    public RewardItemBox(RewardItem rewardItem) {
        this.rewardItem = rewardItem;
    }

    @Override
    protected void modifyItemStackTooltip(List<Component> tooltips) {
        var itemName = tooltips.getFirst().getString();
        tooltips.clear();
        tooltips.addFirst(Resources.YOU_RECEIVE);
        tooltips.add(Component.literal(itemName + ": " + rewardItem.total()));
    }

    @Override
    public Optional<ItemStack> itemStack() {
        return Optional.of(rewardItem.stack());
    }

    @Override
    public String text() {
        return "" + rewardItem.total();
    }

    @Override
    public Color fillColor() {
        return new Color(0x80e0ff);
    }
}
