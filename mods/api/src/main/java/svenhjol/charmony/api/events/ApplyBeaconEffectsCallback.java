package svenhjol.charmony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Custom Fabric event that is fired every time a beacon wants to apply its effects.
 */
@SuppressWarnings("unused")
public interface ApplyBeaconEffectsCallback {
    Event<ApplyBeaconEffectsCallback> EVENT = EventFactory.createArrayBacked(ApplyBeaconEffectsCallback.class, listeners ->
        (level, pos, levels, primaryEffect, secondaryEffect) -> {
            for (var listener : listeners) {
                listener.interact(level, pos, levels, primaryEffect, secondaryEffect);
            }
        });

    void interact(Level level, BlockPos pos, int levels, @Nullable Holder<MobEffect> primaryEffect, Holder<MobEffect> secondaryEffect);
}
