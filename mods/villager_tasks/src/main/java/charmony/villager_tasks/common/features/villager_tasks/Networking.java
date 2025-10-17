package charmony.villager_tasks.common.features.villager_tasks;

import charmony.core.base.Setup;
import charmony.villager_tasks.VillagerTasksMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class Networking extends Setup<VillagerTasks> {
    public Networking(VillagerTasks feature) {
        super(feature);
    }

    public record S2CTasks(Tasks tasks) implements CustomPacketPayload {
        public static Type<S2CTasks> TYPE = new Type<>(VillagerTasksMod.id("send_tasks"));
        public static StreamCodec<FriendlyByteBuf, S2CTasks> CODEC =
            StreamCodec.of(S2CTasks::encode, S2CTasks::decode);

        public static void send(ServerPlayer player, Tasks tasks) {
            ServerPlayNetworking.send(player, new S2CTasks(tasks));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void encode(FriendlyByteBuf buf, S2CTasks self) {
            buf.writeNbt(self.tasks.save());
        }

        private static S2CTasks decode(FriendlyByteBuf buf) {
            var nbt = buf.readNbt();
            if (nbt != null) {
                return new S2CTasks(Tasks.load(nbt));
            }

            throw new RuntimeException("Missing tasks nbt data");
        }
    }
}
