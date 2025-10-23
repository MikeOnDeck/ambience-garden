package charmony.villager_tasks.client.features.villager_tasks.components;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class CollectBox extends AspectBox {
    private final ItemStack stack;
    private final int count;

    public CollectBox(ItemStack stack, int count) {
        this.count = count;
        this.stack = stack;
    }

    @Override
    protected void modifyItemStackTooltip(List<Component> tooltips) {
        var itemName = tooltips.getFirst().getString();
        tooltips.clear();
        tooltips.add(Resources.YOU_COLLECT);
        tooltips.add(Component.literal(itemName + ": " + count));
    }

    @Override
    public Optional<ItemStack> itemStack() {
        return Optional.of(stack);
    }

    @Override
    public String text() {
        return "" + count;
    }

    @Override
    public Color fillColor() {
        return new Color(0xff8080);
    }
}
