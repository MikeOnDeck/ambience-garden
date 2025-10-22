package charmony.villager_tasks.client.features.villager_tasks.components;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CollectItemBox extends ItemBox {
    private final int count;

    public CollectItemBox(ItemStack stack, int count, Font font) {
        super(font, stack, "" + count, new Color(0xffffff).getArgbColor(), new Color(0xff8080).getArgbColor());
        this.count = count;
    }

    @Override
    protected void modifyStackTooltip(List<Component> tooltips) {
        var itemName = tooltips.getFirst().getString();
        tooltips.clear();
        tooltips.add(Resources.YOU_COLLECT);
        tooltips.add(Component.literal(itemName + ": " + count));
    }
}
