package svenhjol.charmony.core.client.mixins.client_respawn_player.login_client;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.api.events.ClientRespawnPlayerCallback;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(
        method = "handleRespawn",
        at = @At("HEAD")
    )
    private void hookHandleRespawn(ClientboundRespawnPacket clientboundRespawnPacket, CallbackInfo ci) {
        ClientRespawnPlayerCallback.EVENT.invoker().interact(clientboundRespawnPacket);
    }
}
