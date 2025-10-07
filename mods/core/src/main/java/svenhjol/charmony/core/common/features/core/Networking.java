package svenhjol.charmony.core.common.features.core;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import svenhjol.charmony.core.Charmony;
import svenhjol.charmony.core.base.Setup;

public class Networking extends Setup<Core> {
    public Networking(Core feature) {
        super(feature);
    }

    public record S2CUsingCharmonyServer() implements CustomPacketPayload {
        public static final Type<S2CUsingCharmonyServer> TYPE = new Type<>(Charmony.id("using_charmony_server"));
        public static final StreamCodec<FriendlyByteBuf, S2CUsingCharmonyServer> CODEC =
            StreamCodec.of(S2CUsingCharmonyServer::encode, S2CUsingCharmonyServer::decode);

        public static void send(ServerPlayer player) {
            ServerPlayNetworking.send(player, new S2CUsingCharmonyServer());
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void encode(FriendlyByteBuf buf, S2CUsingCharmonyServer self) {
            // nothing to encode
        }

        private static S2CUsingCharmonyServer decode(FriendlyByteBuf buf) {
            return new S2CUsingCharmonyServer();
        }
    }
}
