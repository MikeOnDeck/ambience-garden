package charmony.villager_tasks.common.features.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.interfaces.EventListener;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.PlayerHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class Aspect implements EventListener, PlayerHolder {
    @Nullable private Player player;

    @Override
    public void onTick(Player player) {
        this.player = player;
    }

    @Override
    public Optional<Player> getPlayer() {
        return Optional.ofNullable(this.player);
    }

    public abstract String getId();

    public abstract Component getName();
}
