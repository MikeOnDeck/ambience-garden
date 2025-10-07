package svenhjol.charmony.core.common.mixins.data_component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.core.common.CommonRegistry;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract <T extends TooltipProvider> void addToTooltip(DataComponentType<T> dataComponentType, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag);

    @Inject(
        method = "addDetailsToTooltip",
        at = @At("TAIL")
    )
    private void hookAddDetailsToTooltip(Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, @Nullable Player player, TooltipFlag tooltipFlag, Consumer<Component> consumer, CallbackInfo ci) {
        for (var type : CommonRegistry.DATA_COMPONENT_TOOLTIP_PROVIDERS) {
            this.addToTooltip(type, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
        }
    }
}
