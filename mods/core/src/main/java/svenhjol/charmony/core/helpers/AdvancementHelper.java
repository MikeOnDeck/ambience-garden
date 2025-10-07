package svenhjol.charmony.core.helpers;

import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.core.common.features.advancements.Advancements;

@SuppressWarnings("unused")
public final class AdvancementHelper {
    /**
     * Helper method to trigger a charmony advancement.
     */
    public static void trigger(String id, Player player) {
        Advancements.trigger(id, player);
    }
}
