package charmony.villager_tasks.common.mixins.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.VillagerTasks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WanderingTrader.class)
public class WanderingTraderMixin {
    @Inject(
        method = "mobInteract",
        at = @At("HEAD")
    )
    private void hookMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (player instanceof ServerPlayer serverPlayer) {
            VillagerTasks.feature().handlers.makeAvailableTasks(serverPlayer, (AbstractVillager)(Object)this);
        }
    }
}
