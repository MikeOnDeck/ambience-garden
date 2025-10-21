package charmony.villager_tasks.common.features.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.interfaces.EventListener;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.PlayerHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class Aspect implements EventListener, PlayerHolder {
    @Nullable private ServerPlayer player;

    @Override
    public void onTick(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public Optional<ServerPlayer> getPlayer() {
        return Optional.ofNullable(this.player);
    }

    public abstract String getId();

    public abstract Component getName();
}
