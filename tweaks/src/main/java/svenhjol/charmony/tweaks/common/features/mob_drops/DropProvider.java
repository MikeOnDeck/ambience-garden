package svenhjol.charmony.tweaks.common.features.mob_drops;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface DropProvider {
    default Optional<ItemStack> dropWhenKilled(LivingEntity entity, DamageSource source) {
        return Optional.empty();
    }

    default Optional<ItemStack> dropByChance(LivingEntity entity) {
        return Optional.empty();
    }
}
