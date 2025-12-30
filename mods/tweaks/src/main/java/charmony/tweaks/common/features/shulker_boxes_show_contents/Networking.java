package charmony.tweaks.common.features.shulker_boxes_show_contents;

import charmony.core.Charmony;
import charmony.core.base.Setup;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class Networking extends Setup<ShulkerBoxesShowContents> {
    public Networking(ShulkerBoxesShowContents feature) {
        super(feature);
    }

    // Server-to-client
    public record S2CShowContents(List<ItemStack> items) implements CustomPacketPayload {
        public static final String ITEMS_TAG = "items";
        public static Type<S2CShowContents> TYPE = new Type<>(Charmony.id("show_shulker_box_contents"));
        static StreamCodec<RegistryFriendlyByteBuf, S2CShowContents> CODEC = StreamCodec.of(S2CShowContents::encode, S2CShowContents::decode);

        public static void send(ServerPlayer player, List<ItemStack> items) {
            ServerPlayNetworking.send(player, new S2CShowContents(items));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public List<ItemStack> getItems() {
            return items;
        }

        private static S2CShowContents decode(RegistryFriendlyByteBuf buf) {
            var nbt = buf.readNbt();

            if (nbt == null) {
                throw new IllegalStateException("NBT data missing from packet!");
            }

            var items = nbt.read(ITEMS_TAG, ItemStack.OPTIONAL_CODEC.listOf(), RegistryOps.create(NbtOps.INSTANCE, buf.registryAccess())).orElseThrow();
            return new S2CShowContents(items);
        }

        private static void encode(RegistryFriendlyByteBuf buf, S2CShowContents self) {
            var nbt = new CompoundTag();
            nbt.store(ITEMS_TAG, ItemStack.OPTIONAL_CODEC.listOf(), RegistryOps.create(NbtOps.INSTANCE, buf.registryAccess()), self.items);
            buf.writeNbt(nbt);
        }
    }

    // Client-to-server
    public record C2SRequestContents(BlockPos pos) implements CustomPacketPayload {
        public static Type<C2SRequestContents> TYPE = new Type<>(Charmony.id("request_shulker_box_contents"));
        static StreamCodec<RegistryFriendlyByteBuf, C2SRequestContents> CODEC = StreamCodec.of(C2SRequestContents::encode, C2SRequestContents::decode);

        public static void send(BlockPos pos) {
            ClientPlayNetworking.send(new C2SRequestContents(pos));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public BlockPos getPos() {
            return pos;
        }

        private static C2SRequestContents decode(RegistryFriendlyByteBuf buf) {
            var pos = buf.readBlockPos();
            return new C2SRequestContents(pos);
        }

        private static void encode(RegistryFriendlyByteBuf buf, C2SRequestContents self) {
            buf.writeBlockPos(self.pos);
        }
    }
}
