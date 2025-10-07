package svenhjol.charmony.core.client.features.core;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.features.core.Networking.S2CUsingCharmonyServer;

@SuppressWarnings("unused")
public class Handlers extends Setup<Core> {
    private boolean usesCharmonyServer = false;
    private boolean hasCheckedCharmonyServer = false;
    private int ticksSinceLogin = 0;

    public Handlers(Core feature) {
        super(feature);
    }

    /**
     * If the client receives this packet we can safely depend on charmony being present on the server.
     */
    public void usingCharmonyServer(S2CUsingCharmonyServer packet, ClientPlayNetworking.Context context) {
        usesCharmonyServer = true;
    }

    /**
     * On client login to the server we reset the client's state.
     */
    public void clientLogin(ClientboundLoginPacket packet) {
        hasCheckedCharmonyServer = false;
        usesCharmonyServer = false; // We have to receive the charmony server packet to set this to true.
        ticksSinceLogin = 0;
    }

    /**
     * On client start we perform some deferred registrations.
     */
    public void clientStart(Minecraft minecraft) {
        // Register deferred particles.
        feature().registers.registerDeferredParticles(minecraft);
    }

    public boolean usesCharmonyServer() {
        return usesCharmonyServer;
    }

    public void clientTick(Minecraft minecraft) {
        if (minecraft.player == null) {
            // Not in game, ignore.
            return;
        }

        if (!hasCheckedCharmonyServer) {
            if (usesCharmonyServer) {
                feature().log().info("""
                    
                    ----------------------------------------------
                    Your client is connected to a charmony server.
                    All enabled Charmony features will work.
                    ----------------------------------------------""");
                hasCheckedCharmonyServer = true;
            }
            if (!usesCharmonyServer && ticksSinceLogin++ > 20) {
                feature().log().info("""
                    
                    ----------------------------------------------
                    Your client is connected to a vanilla server.
                    Only Charmony client features will work.
                    ----------------------------------------------""");
                hasCheckedCharmonyServer = true;
            }
        }
    }
}
