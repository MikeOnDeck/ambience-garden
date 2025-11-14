package charmony.rune_dictionary.common.features.rune_dictionary;

import charmony.core.base.Setup;
import charmony.rune_dictionary.RuneDictionaryMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

@SuppressWarnings({"unused", "NullableProblems"})
public class Networking extends Setup<RuneDictionary> {
    public Networking(RuneDictionary feature) {
        super(feature);
    }

    /**
     * Send the registered dictionary of words to the client.
     *
     * @param dictionary Hash map of rune words to their registered objects.
     */
    public record S2CDictionary(Map<Identifier, String> dictionary) implements CustomPacketPayload {
        public static Type<S2CDictionary> TYPE = new Type<>(RuneDictionaryMod.id("send_dictionary"));

        public static StreamCodec<FriendlyByteBuf, S2CDictionary> CODEC =
            StreamCodec.of(S2CDictionary::encode, S2CDictionary::decode);

        public static void send(ServerPlayer player, Map<Identifier, String> dictionary) {
            ServerPlayNetworking.send(player, new S2CDictionary(dictionary));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static void encode(FriendlyByteBuf buf, S2CDictionary self) {
            buf.writeMap(self.dictionary, FriendlyByteBuf::writeIdentifier, FriendlyByteBuf::writeUtf);
        }

        public static S2CDictionary decode(FriendlyByteBuf buf) {
            return new S2CDictionary(buf.readMap(FriendlyByteBuf::readIdentifier, FriendlyByteBuf::readUtf));
        }
    }

    /**
     * Sends the player's knowledge data to the client.
     *
     * @param knowledge
     */
    public record S2CKnowledge(Knowledge knowledge) implements CustomPacketPayload {
        public static Type<S2CKnowledge> TYPE = new Type<>(RuneDictionaryMod.id("send_knowledge"));
        public static StreamCodec<FriendlyByteBuf, S2CKnowledge> CODEC =
            StreamCodec.of(S2CKnowledge::encode, S2CKnowledge::decode);

        public static void send(ServerPlayer player, Knowledge knowledge) {
            ServerPlayNetworking.send(player, new S2CKnowledge(knowledge));
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void encode(FriendlyByteBuf buf, S2CKnowledge self) {
            buf.writeNbt(self.knowledge.save());
        }

        private static S2CKnowledge decode(FriendlyByteBuf buf) {
            var nbt = buf.readNbt();
            if (nbt != null) {
                return new S2CKnowledge(Knowledge.load(nbt));
            }
            throw new RuntimeException("Missing knowledge nbt data");
        }
    }

    /**
     * Sends a request to the server for the player's knowledge.
     * This isn't used by the mod and it's intended for other mods to use when the knowledge needs to be synced.
     */
    public record C2SRequestKnowledge() implements CustomPacketPayload {
        public static Type<C2SRequestKnowledge> TYPE = new Type<>(RuneDictionaryMod.id("request_knowledge"));
        public static StreamCodec<FriendlyByteBuf, C2SRequestKnowledge> CODEC =
            StreamCodec.of(C2SRequestKnowledge::encode, C2SRequestKnowledge::decode);

        public static void send() {
            ClientPlayNetworking.send(new C2SRequestKnowledge());
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void encode(FriendlyByteBuf buf, C2SRequestKnowledge self) {
            // no op
        }

        private static C2SRequestKnowledge decode(FriendlyByteBuf buf) {
            return new C2SRequestKnowledge();
        }
    }

    /**
     * Sends a request to the server for the registered dictionary.
     * This isn't used by the mod and it's intended for other mods to use when the dictionary needs to be synced.
     */
    public record C2SRequestDictionary() implements CustomPacketPayload {
        public static Type<C2SRequestDictionary> TYPE = new Type<>(RuneDictionaryMod.id("request_dictionary"));
        public static StreamCodec<FriendlyByteBuf, C2SRequestDictionary> CODEC =
            StreamCodec.of(C2SRequestDictionary::encode, C2SRequestDictionary::decode);

        public static void send() {
            ClientPlayNetworking.send(new C2SRequestDictionary());
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void encode(FriendlyByteBuf buf, C2SRequestDictionary self) {
            // no op
        }

        private static C2SRequestDictionary decode(FriendlyByteBuf buf) {
            return new C2SRequestDictionary();
        }
    }
}
