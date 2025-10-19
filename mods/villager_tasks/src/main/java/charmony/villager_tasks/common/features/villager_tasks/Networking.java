package charmony.villager_tasks.common.features.villager_tasks;

import charmony.core.base.Setup;
import charmony.villager_tasks.VillagerTasksMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class Networking extends Setup<VillagerTasks> {
    public Networking(VillagerTasks feature) {
        super(feature);
    }

    public record S2CSendMerchantInteraction(UUID uuid) implements CustomPacketPayload {
        public static Type<S2CSendMerchantInteraction> TYPE = new Type<>(VillagerTasksMod.id("send_merchant_interaction"));
        public static StreamCodec<FriendlyByteBuf, S2CSendMerchantInteraction> CODEC =
            StreamCodec.of(S2CSendMerchantInteraction::encode, S2CSendMerchantInteraction::decode);

        public static void send(ServerPlayer player, UUID uuid) {
            ServerPlayNetworking.send(player, new S2CSendMerchantInteraction(uuid));
        }

        private static void encode(FriendlyByteBuf buf, S2CSendMerchantInteraction self) {
            buf.writeUUID(self.uuid);
        }

        private static S2CSendMerchantInteraction decode(FriendlyByteBuf buf) {
            var uuid = buf.readUUID();
            return new S2CSendMerchantInteraction(uuid);
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record S2CSendAvailableTasks(Tasks tasks) implements CustomPacketPayload {
        public static Type<S2CSendAvailableTasks> TYPE = new Type<>(VillagerTasksMod.id("send_available_tasks"));
        public static StreamCodec<FriendlyByteBuf, S2CSendAvailableTasks> CODEC =
            StreamCodec.of(S2CSendAvailableTasks::encode, S2CSendAvailableTasks::decode);

        public static void send(ServerPlayer player, Tasks tasks) {
            ServerPlayNetworking.send(player, new S2CSendAvailableTasks(tasks));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void encode(FriendlyByteBuf buf, S2CSendAvailableTasks self) {
            buf.writeNbt(self.tasks.save());
        }

        private static S2CSendAvailableTasks decode(FriendlyByteBuf buf) {
            var nbt = buf.readNbt();

            if (nbt != null) {
                return new S2CSendAvailableTasks(Tasks.load(nbt));
            }

            throw new RuntimeException("Missing S2CSendAvailableTasks NBT data");
        }
    }

    public record S2CSendActiveTasks(Tasks tasks) implements CustomPacketPayload {
        public static Type<S2CSendActiveTasks> TYPE = new Type<>(VillagerTasksMod.id("send_active_tasks"));
        public static StreamCodec<FriendlyByteBuf, S2CSendActiveTasks> CODEC =
            StreamCodec.of(S2CSendActiveTasks::encode, S2CSendActiveTasks::decode);

        public static void send(ServerPlayer player, Tasks tasks) {
            ServerPlayNetworking.send(player, new S2CSendActiveTasks(tasks));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void encode(FriendlyByteBuf buf, S2CSendActiveTasks self) {
            buf.writeNbt(self.tasks.save());
        }

        private static S2CSendActiveTasks decode(FriendlyByteBuf buf) {
            var nbt = buf.readNbt();
            if (nbt != null) {
                return new S2CSendActiveTasks(Tasks.load(nbt));
            }

            throw new RuntimeException("Missing S2CSendActiveTasks NBT data");
        }
    }
}
