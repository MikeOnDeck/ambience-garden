package svenhjol.charmony.core.client.mixins.client_login_player;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.api.events.ClientLoginPlayerCallback;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(
        method = "handleLogin",
        at = @At("HEAD")
    )
    private void hookHandleLogin(ClientboundLoginPacket clientboundLoginPacket, CallbackInfo ci) {
        ClientLoginPlayerCallback.EVENT.invoker().interact(clientboundLoginPacket);
    }
}
