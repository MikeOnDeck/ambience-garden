package svenhjol.charmony.tweaks.common.features.item_repairing;

import net.minecraft.server.level.ServerPlayer;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<ItemRepairing> {
    public Advancements(ItemRepairing feature) {
        super(feature);
    }

    public void repairedTrident(ServerPlayer player) {
        AdvancementHelper.trigger("repaired_trident", player);
    }

    public void repairedNetherite(ServerPlayer player) {
        AdvancementHelper.trigger("repaired_netherite", player);
    }
}
