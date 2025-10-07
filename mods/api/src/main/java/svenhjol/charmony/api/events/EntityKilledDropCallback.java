package svenhjol.charmony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Custom Fabric event that is fired when the living entity is killed to calculate drops.
 */
@SuppressWarnings("unused")
public interface EntityKilledDropCallback {
    Event<EntityKilledDropCallback> EVENT = EventFactory.createArrayBacked(EntityKilledDropCallback.class, listeners -> (livingEntity, damageSource) -> {
        for (EntityKilledDropCallback listener : listeners) {
            var result = listener.interact(livingEntity, damageSource);
            if (result != InteractionResult.PASS) {
                return result;
            }
        }
        return InteractionResult.PASS;
    });

    InteractionResult interact(LivingEntity livingEntity, DamageSource damageSource);
}
