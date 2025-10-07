package svenhjol.charmony.core.common.features.advancements;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import svenhjol.charmony.core.base.Setup;

@SuppressWarnings("unused")
public class Handlers extends Setup<Advancements> {
    public Handlers(Advancements feature) {
        super(feature);
    }

    /**
     * Trigger the "player_joined" advancement criteria on player login.
     * This is the foundation criteria for all charmony-based advancements.
     */
    public void playerJoin(ServerGamePacketListenerImpl listener, PacketSender packetSender, MinecraftServer server) {
        var player = listener.getPlayer();
        Advancements.trigger("player_joined", player);
    }
}
