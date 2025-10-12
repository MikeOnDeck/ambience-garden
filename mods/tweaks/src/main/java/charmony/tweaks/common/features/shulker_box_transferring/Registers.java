package charmony.tweaks.common.features.shulker_box_transferring;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ItemLike;
import charmony.core.base.Setup;
import charmony.core.common.CommonRegistry;
import charmony.api.core.Side;
import charmony.api.events.ItemDragDropCallback;
import charmony.core.helpers.TagHelper;
import charmony.tweaks.common.features.shulker_box_transferring.Networking.C2SAddItemToShulkerBox;
import charmony.tweaks.common.features.shulker_box_transferring.Networking.C2SReorderShulkerBoxItems;

import java.util.ArrayList;
import java.util.List;

public class Registers extends Setup<ShulkerBoxTransferring> {
    public final List<ItemLike> blacklist = new ArrayList<>();

    public Registers(ShulkerBoxTransferring feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            var registry = CommonRegistry.forFeature(feature());

            ItemDragDropCallback.EVENT.register(feature().handlers::itemDragDrop);
            ServerWorldEvents.LOAD.register(this::worldLoad);

            // Client-to-server packets.
            registry.packetSender(Side.Client, C2SAddItemToShulkerBox.TYPE, C2SAddItemToShulkerBox.CODEC);
            registry.packetSender(Side.Client, C2SReorderShulkerBoxItems.TYPE, C2SReorderShulkerBoxItems.CODEC);

            // Handle packets being sent from the client.
            registry.packetReceiver(C2SAddItemToShulkerBox.TYPE, () -> feature().handlers::handleAddItemToShulkerBoxPacket);
            registry.packetReceiver(C2SReorderShulkerBoxItems.TYPE, () -> feature().handlers::handleReorderShulkerBoxItemsPacket);
        };
    }

    private void worldLoad(MinecraftServer server, ServerLevel level) {
        for (var block : TagHelper.getValues(BuiltInRegistries.BLOCK, BlockTags.SHULKER_BOXES)) {
            if (!blacklist.contains(block)) {
                blacklist.add(block);
            }
        }
    }
}
