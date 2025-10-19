package charmony.villager_tasks.client.features.villager_tasks;

import charmony.core.base.Setup;
import charmony.villager_tasks.client.features.villager_tasks.screens.AvailableTasksScreen;
import charmony.villager_tasks.common.features.villager_tasks.Networking;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.Tasks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class Handlers extends Setup<VillagerTasks> {
    @Nullable private Tasks availableTasks = null;
    @Nullable private UUID lastMerchantInteraction = null;

    public Handlers(VillagerTasks feature) {
        super(feature);
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
            b -> {
                merchantScreen.onClose();
                minecraft.setScreen(new AvailableTasksScreen(merchant));
            }));
    }

    public void handleReceiveActiveTasks(Player player, Networking.S2CSendActiveTasks payload) {
        feature().common.get().handlers.setActiveTasks(player, payload.tasks());
    }

    public void handleReceiveAvailableTasks(Player player, Networking.S2CSendAvailableTasks payload) {
        availableTasks = payload.tasks();
    }

    public void handleReceiveMerchantInteraction(Player player, Networking.S2CSendMerchantInteraction payload) {
        lastMerchantInteraction = payload.uuid();
    }

    public Optional<Tasks> getAvailableTasks() {
        return Optional.ofNullable(availableTasks);
    }

    public Optional<UUID> getLastMerchantInteraction() {
        return Optional.ofNullable(lastMerchantInteraction);
    }

    public void acceptTask(Task task) {
        log().info("acceptTask: " + task.getDefinitionId());
    }
}
