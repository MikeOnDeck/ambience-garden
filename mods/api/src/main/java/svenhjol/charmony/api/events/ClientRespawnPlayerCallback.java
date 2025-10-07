package svenhjol.charmony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;

/**
 * Custom Fabric event that is fired whenever the ClientPacketListener handleRespawn method is called.
 * Typically this is when a client player is respawned after death.
 */
@SuppressWarnings("unused")
public interface ClientRespawnPlayerCallback {
    Event<ClientRespawnPlayerCallback> EVENT = EventFactory.createArrayBacked(ClientRespawnPlayerCallback.class,
        listeners -> packet -> {
            for (ClientRespawnPlayerCallback listener : listeners) {
                listener.interact(packet);
            }
        });

    void interact(ClientboundRespawnPacket packet);
}
