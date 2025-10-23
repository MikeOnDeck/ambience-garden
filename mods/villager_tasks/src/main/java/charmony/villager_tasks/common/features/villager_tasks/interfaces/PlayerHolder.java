package charmony.villager_tasks.common.features.villager_tasks.interfaces;

import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public interface PlayerHolder {
    Optional<Player> getPlayer();
}
