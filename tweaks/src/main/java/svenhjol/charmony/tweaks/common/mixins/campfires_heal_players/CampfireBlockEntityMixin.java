package svenhjol.charmony.tweaks.common.mixins.campfires_heal_players;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.tweaks.common.features.campfires_heal_players.CampfiresHealPlayers;

@Mixin(CampfireBlockEntity.class)
public class CampfireBlockEntityMixin {
    @Inject(
        method = "cookTick",
        at = @At("HEAD")
    )
    private static void hookCookTick(ServerLevel level, BlockPos pos, BlockState blockState, CampfireBlockEntity campfireBlockEntity,
                                     RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> cachedCheck, CallbackInfo ci) {
        CampfiresHealPlayers.feature().handlers.tryRegeneratePlayersAroundFire(level, pos);
    }
}
