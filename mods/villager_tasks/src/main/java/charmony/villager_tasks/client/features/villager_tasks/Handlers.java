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
    @Nullable private Tasks activeTasks = null;
    @Nullable private Tasks availableTasks = null;
    @Nullable private UUID lastVillagerInteraction = null;

    public Handlers(VillagerTasks feature) {
        super(feature);
    }

    public void setupScreen(Screen screen) {
        if (!(screen instanceof MerchantScreen merchantScreen)) {
            return;
        }

        var midX = merchantScreen.width / 2;
        var baseY = merchantScreen.topPos + 174;
        var minecraft = Minecraft.getInstance();

        screen.addRenderableWidget(new Buttons.ViewTasksButton(
            midX - (Buttons.ViewTasksButton.WIDTH / 2),
            baseY,
            b -> {
                merchantScreen.onClose();
                minecraft.setScreen(new AvailableTasksScreen());
            }));
    }

    public void handleReceiveActiveTasks(Player player, Networking.S2CSendActiveTasks payload) {
        var tasks = payload.tasks();
        this.activeTasks = tasks;

        log().info("Client received " + tasks.tasks().size() + " active tasks.");
    }

    public void handleReceiveAvailableTasks(Player player, Networking.S2CSendAvailableTasks payload) {
        var tasks = payload.tasks();
        this.availableTasks = tasks;

        log().info("Client received " + tasks.tasks().size() + " available tasks.");
    }

    public void handleReceiveVillagerInteraction(Player player, Networking.S2CSendVillagerInteraction payload) {
        var uuid = payload.uuid();
        this.lastVillagerInteraction = payload.uuid();

        log().info("Client received villager interaction with UUID: " + uuid);
    }

    public Optional<Tasks> getActiveTasks() {
        return Optional.ofNullable(activeTasks);
    }

    public Optional<Tasks> getAvailableTasks() {
        return Optional.ofNullable(availableTasks);
    }

    public Optional<UUID> getLastVillagerInteraction() {
        return Optional.ofNullable(lastVillagerInteraction);
    }

    public void acceptTask(Task task) {
        Networking.C2SAcceptTask.send(task.id);
    }
}
