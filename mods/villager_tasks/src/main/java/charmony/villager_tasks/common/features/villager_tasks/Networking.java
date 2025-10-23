package charmony.villager_tasks.common.features.villager_tasks;

import charmony.core.base.Setup;
import charmony.villager_tasks.VillagerTasksMod;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskQuery;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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

    public record S2CSendVillagerInteraction(UUID uuid) implements CustomPacketPayload {
        public static Type<S2CSendVillagerInteraction> TYPE = new Type<>(VillagerTasksMod.id("send_villager_interaction"));
        public static StreamCodec<FriendlyByteBuf, S2CSendVillagerInteraction> CODEC =
            StreamCodec.of(S2CSendVillagerInteraction::encode, S2CSendVillagerInteraction::decode);

        public static void send(ServerPlayer player, UUID uuid) {
            ServerPlayNetworking.send(player, new S2CSendVillagerInteraction(uuid));
        }

        private static void encode(FriendlyByteBuf buf, S2CSendVillagerInteraction self) {
            buf.writeUUID(self.uuid);
        }

        private static S2CSendVillagerInteraction decode(FriendlyByteBuf buf) {
            var uuid = buf.readUUID();
            return new S2CSendVillagerInteraction(uuid);
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

    public record C2SQueryTask(TaskQuery query, UUID id) implements CustomPacketPayload {
        public static Type<C2SQueryTask> TYPE = new Type<>(VillagerTasksMod.id("query_task"));
        public static StreamCodec<FriendlyByteBuf, C2SQueryTask> CODEC =
            StreamCodec.of(C2SQueryTask::encode, C2SQueryTask::decode);

        public static void send(TaskQuery query, UUID id) {
            ClientPlayNetworking.send(new C2SQueryTask(query, id));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void encode(FriendlyByteBuf buf, C2SQueryTask self) {
            buf.writeEnum(self.query);
            buf.writeUUID(self.id);
        }

        private static C2SQueryTask decode(FriendlyByteBuf buf) {
            var query = buf.readEnum(TaskQuery.class);
            var id = buf.readUUID();
            return new C2SQueryTask(query, id);
        }
    }
}
