package svenhjol.charmony.api.tweaks;

import net.minecraft.world.entity.player.Player;

import java.util.function.Function;

/**
 * Attach to a class to let it determine if a player can automatically pick up an item after harvesting it.
 */
@SuppressWarnings("unused")
public interface AutoPickupItemProvider {
    Function<Player, Boolean> getAutoPickupItemChecks();
}
