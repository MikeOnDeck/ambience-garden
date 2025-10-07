package svenhjol.charmony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;

/**
 * Custom Fabric event that is fired when an entity jumps.
 */
@SuppressWarnings("unused")
public interface EntityJumpCallback {
    Event<EntityJumpCallback> EVENT = EventFactory.createArrayBacked(EntityJumpCallback.class, listeners ->
        entity -> {
            for (var listener : listeners) {
                listener.interact(entity);
            }
        });

    void interact(LivingEntity entity);
}
