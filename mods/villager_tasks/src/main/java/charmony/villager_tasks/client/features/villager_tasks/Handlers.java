package charmony.villager_tasks.client.features.villager_tasks;

import charmony.core.base.Setup;
import charmony.villager_tasks.common.features.villager_tasks.Networking.S2CTasks;
import net.minecraft.world.entity.player.Player;

public class Handlers extends Setup<VillagerTasks> {
    public Handlers(VillagerTasks feature) {
        super(feature);
    }

    public void handleTasks(Player player, S2CTasks payload) {
        feature().common.get().handlers.setTasks(player, payload.tasks());
    }
}
