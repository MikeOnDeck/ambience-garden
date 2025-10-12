package charmony.tweaks.common.features.mob_drops;

import net.minecraft.server.level.ServerPlayer;
import charmony.core.base.Setup;
import charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<MobDrops> {
    public Advancements(MobDrops feature) {
        super(feature);
    }

    public void droppedCobwebs(ServerPlayer player) {
        AdvancementHelper.trigger("dropped_cobwebs", player);
    }

    public void droppedSand(ServerPlayer player) {
        AdvancementHelper.trigger("dropped_sand", player);
    }

    public void droppedPotionOfLuck(ServerPlayer player) {
        AdvancementHelper.trigger("dropped_potion_of_luck", player);
    }
}
