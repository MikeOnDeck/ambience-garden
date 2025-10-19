package charmony.villager_tasks.common.features.villager_tasks.interfaces;

import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public interface PlayerHolder {
    Optional<ServerPlayer> getPlayer();
}
