package svenhjol.charmony.tweaks.common.mixins.animal_reviving;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charmony.tweaks.common.features.animal_reviving.AnimalReviving;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyReturnValue(
            method = "shouldDropLoot",
            at = @At("RETURN")
    )
    private boolean hookShouldDropLoot(boolean original) {
        if (this instanceof OwnableEntity ownable
                && ownable.getOwner() != null) {
            return AnimalReviving.feature().dropLootOnDeath();
        }
        return original;
    }

    @ModifyReturnValue(
            method = "shouldDropExperience",
            at = @At("RETURN")
    )
    private boolean hookShouldDropExperience(boolean original) {
        if (this instanceof OwnableEntity ownable
                && ownable.getOwner() != null) {
            return AnimalReviving.feature().dropExperienceOnDeath();
        }
        return original;
    }
}
