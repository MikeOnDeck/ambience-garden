package charmony.villager_tasks.client.features.villager_tasks;

import charmony.core.base.Setup;
import charmony.villager_tasks.client.features.villager_tasks.screens.AvailableTasksScreen;
import charmony.villager_tasks.common.features.villager_tasks.Networking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.world.entity.player.Player;

public class Handlers extends Setup<VillagerTasks> {
    public Handlers(VillagerTasks feature) {
        super(feature);
    }

    public void handleReceiveActiveTasks(Player player, Networking.S2CSendActiveTasks payload) {
        feature().common.get().handlers.setActiveTasks(player, payload.tasks());
    }

    public void setupScreen(Screen screen) {
        if (!(screen instanceof MerchantScreen merchantScreen)) {
            return;
        }

        var menu = merchantScreen.getMenu();
        var midX = merchantScreen.width / 2;
        var baseY = merchantScreen.topPos + 174;
        var minecraft = Minecraft.getInstance();
        var merchant = menu.trader;

        screen.addRenderableWidget(new Buttons.ViewTasksButton(
            midX - (Buttons.ViewTasksButton.WIDTH / 2),
            baseY,
            b -> minecraft.setScreen(new AvailableTasksScreen(merchant))));
    }

    public void handleReceiveAvailableTasks(Player player, Networking.S2CSendAvailableTasks payload) {
        // TODO: Hold this in client handlers, render on AvailableTasksScreen.
    }
}
