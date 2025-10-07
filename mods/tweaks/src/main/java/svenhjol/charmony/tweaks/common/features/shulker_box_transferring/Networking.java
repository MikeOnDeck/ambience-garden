package svenhjol.charmony.tweaks.common.features.shulker_box_transferring;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import svenhjol.charmony.core.Charmony;
import svenhjol.charmony.core.base.Setup;

public final class Networking extends Setup<ShulkerBoxTransferring> {
    public Networking(ShulkerBoxTransferring feature) {
        super(feature);
    }

    public record C2SAddItemToShulkerBox() implements CustomPacketPayload {
        public static Type<C2SAddItemToShulkerBox> TYPE = new Type<>(Charmony.id("add_item_to_shulker_box"));
        static StreamCodec<FriendlyByteBuf, C2SAddItemToShulkerBox> CODEC = StreamCodec.of(C2SAddItemToShulkerBox::encode, C2SAddItemToShulkerBox::decode);

        public static void send() {
            ClientPlayNetworking.send(new C2SAddItemToShulkerBox());
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static C2SAddItemToShulkerBox decode(FriendlyByteBuf buf) {
            return new C2SAddItemToShulkerBox();
        }

        private static void encode(FriendlyByteBuf buf, C2SAddItemToShulkerBox self) {
            // no op
        }
    }

    public record C2SReorderShulkerBoxItems(int slot, int direction) implements CustomPacketPayload {
        public static Type<C2SReorderShulkerBoxItems> TYPE = new Type<>(Charmony.id("reorder_shulker_box_items"));
        static StreamCodec<FriendlyByteBuf, C2SReorderShulkerBoxItems> CODEC = StreamCodec.of(C2SReorderShulkerBoxItems::encode, C2SReorderShulkerBoxItems::decode);

        public static void send(int slot, int direction) {
            ClientPlayNetworking.send(new C2SReorderShulkerBoxItems(slot, direction));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static C2SReorderShulkerBoxItems decode(FriendlyByteBuf buf) {
            return new C2SReorderShulkerBoxItems(buf.readInt(), buf.readInt());
        }

        private static void encode(FriendlyByteBuf buf, C2SReorderShulkerBoxItems self) {
            buf.writeInt(self.slot);
            buf.writeInt(self.direction);
        }
    }
}
