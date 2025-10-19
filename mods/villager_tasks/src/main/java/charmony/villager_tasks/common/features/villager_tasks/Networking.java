package charmony.villager_tasks.common.features.villager_tasks;

import charmony.core.base.Setup;
import charmony.villager_tasks.VillagerTasksMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
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

    public record C2SAcceptTask(ResourceLocation definitionId, UUID merchant) implements CustomPacketPayload {
        public static Type<C2SAcceptTask> TYPE = new Type<>(VillagerTasksMod.id("accept_task"));
        public static StreamCodec<FriendlyByteBuf, C2SAcceptTask> CODEC =
            StreamCodec.of(C2SAcceptTask::encode, C2SAcceptTask::decode);

        public static void send(ResourceLocation definitionId, UUID merchant) {
            ClientPlayNetworking.send(new C2SAcceptTask(definitionId, merchant));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void encode(FriendlyByteBuf buf, C2SAcceptTask self) {
            buf.writeResourceLocation(self.definitionId);
            buf.writeUUID(self.merchant);
        }

        private static C2SAcceptTask decode(FriendlyByteBuf buf) {
            var definitionId = buf.readResourceLocation();
            var merchant = buf.readUUID();
            return new C2SAcceptTask(definitionId, merchant);
        }
    }
}
