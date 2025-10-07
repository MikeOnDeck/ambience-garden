package svenhjol.charmony.tweaks.client.features.repair_cost_visible;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import svenhjol.charmony.core.base.Setup;

import java.util.List;

public class Handlers extends Setup<RepairCostVisible> {
    public Handlers(RepairCostVisible feature) {
        super(feature);
    }

    public List<Component> addRepairCostToTooltip(ItemStack stack, List<Component> tooltip) {
        var repairCost = stack.get(DataComponents.REPAIR_COST);
        if (repairCost != null && repairCost > 0) {
            tooltip.add(Component.empty()); // A blank line.
            tooltip.add(Component.translatable("gui.charmony.repair_cost", repairCost)
                .withStyle(ChatFormatting.GRAY));
        }

        return tooltip;
    }
}
