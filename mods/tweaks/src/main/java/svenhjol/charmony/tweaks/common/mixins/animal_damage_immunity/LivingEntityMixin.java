package svenhjol.charmony.tweaks.common.mixins.animal_damage_immunity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charmony.tweaks.common.features.animal_damage_immunity.AnimalDamageImmunity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @WrapMethod(
        method = "hurtServer"
    )
    private boolean hurt(ServerLevel serverLevel, DamageSource damageSource, float f, Operation<Boolean> original) {
        var shouldHurt = AnimalDamageImmunity.feature().handlers.shouldHurt((LivingEntity)(Object)this, damageSource);
        if (!shouldHurt) {
            return false;
        }
        return original.call(serverLevel, damageSource, f);
    }
}
