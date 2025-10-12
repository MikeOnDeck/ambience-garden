package charmony.tweaks.common.features.campfires_heal_players;

import net.minecraft.server.level.ServerPlayer;
import charmony.core.base.Setup;
import charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<CampfiresHealPlayers> {
    public Advancements(CampfiresHealPlayers feature) {
        super(feature);
    }

    public void healedNearCampfire(ServerPlayer player) {
        AdvancementHelper.trigger("healed_near_campfire", player);
    }
}
