package charmony.villager_tasks.client.features.villager_tasks.components;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class RewardXpBox extends ItemBox {
    public RewardXpBox(String text, Font font) {
        super(font, new ItemStack(Items.EXPERIENCE_BOTTLE), text, new Color(0xffffff).getArgbColor(), new Color(0x80ffc0).getArgbColor());
    }

    @Override
    protected void modifyStackTooltip(List<Component> tooltips) {
        tooltips.clear();
        tooltips.add(Resources.YOU_RECEIVE);
        tooltips.add(Component.translatable("gui.charmony.villager_tasks.experience_levels", text));
    }
}
