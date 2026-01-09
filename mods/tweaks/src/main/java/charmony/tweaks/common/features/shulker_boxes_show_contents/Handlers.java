package charmony.tweaks.common.features.shulker_boxes_show_contents;

import charmony.core.base.Setup;
import charmony.tweaks.common.features.shulker_boxes_show_contents.Networking.C2SRequestContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

import java.util.ArrayList;

public class Handlers extends Setup<ShulkerBoxesShowContents> {
    public Handlers(ShulkerBoxesShowContents feature) {
        super(feature);
    }

    /**
     * Called when the server receives a client request to show the contents of a shulker box.
     */
    public void handleRequestContents(Player player, C2SRequestContents payload) {
        if (!feature().showSameItemLabel()) return;

        var level = player.level();
        var pos = payload.getPos();
        var blockEntity = level.getBlockEntity(pos);

        if (!(player instanceof ServerPlayer serverPlayer)) return;

        if (blockEntity instanceof ShulkerBoxBlockEntity shulkerBox) {
            var name = shulkerBox.getDisplayName();
            var items = new ArrayList<ItemStack>();

            for (var i = 0; i < shulkerBox.getContainerSize(); i++) {
                items.add(shulkerBox.getItem(i));
            }

            Networking.S2CShowContents.send(serverPlayer, name.getString(), items);
        }
    }
}
