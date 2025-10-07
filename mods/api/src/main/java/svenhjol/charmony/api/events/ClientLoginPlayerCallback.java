package svenhjol.charmony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;

/**
 * Custom Fabric event that is fired whenever the ClientPacketListener handleLogin method is called.
 * Typically this is when a client player first enters the world.
 */
@SuppressWarnings("unused")
public interface ClientLoginPlayerCallback {
    Event<ClientLoginPlayerCallback> EVENT = EventFactory.createArrayBacked(ClientLoginPlayerCallback.class,
        listeners -> packet -> {
            for (ClientLoginPlayerCallback listener : listeners) {
                listener.interact(packet);
            }
        });

    void interact(ClientboundLoginPacket packet);
}
