package svenhjol.charmony.tweaks.client.mixins.repair_cost_visible;

import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.tweaks.client.features.repair_cost_visible.RepairCostVisible;

import java.util.List;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu> {
    public AnvilScreenMixin(AnvilMenu handler, Inventory playerInventory, Component title, ResourceLocation texture) {
        super(handler, playerInventory, title, texture);
    }

    @Override
    public List<Component> getTooltipFromContainerItem(ItemStack stack) {
        var tooltip = super.getTooltipFromContainerItem(stack);
        return Mod.getSidedFeature(RepairCostVisible.class).handlers.addRepairCostToTooltip(stack, tooltip);
    }
}
