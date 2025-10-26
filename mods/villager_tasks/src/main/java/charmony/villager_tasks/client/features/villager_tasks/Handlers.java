package charmony.villager_tasks.client.features.villager_tasks;

import charmony.core.base.Setup;
import charmony.villager_tasks.client.features.villager_tasks.screens.ActiveTasksScreen;
import charmony.villager_tasks.client.features.villager_tasks.screens.AvailableTasksScreen;
import charmony.villager_tasks.common.features.villager_tasks.Networking;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.Tasks;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskQuery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class Handlers extends Setup<VillagerTasks> {
    private Tasks activeTasks = Tasks.EMPTY;
    private Tasks availableTasks = Tasks.EMPTY;
    private UUID lastVillagerInteraction = UUID.randomUUID();
    public Map<Screen, Consumer<Tasks>> onActiveTasksUpdate = new HashMap<>();

    public Handlers(VillagerTasks feature) {
        super(feature);
    }

    public void clientTick(Minecraft minecraft) {
        if (minecraft != null && minecraft.player instanceof Player player) {
            activeTasks.tasks().forEach(task -> task.onTick(player));
        }
    }

    public void setupScreen(Screen screen) {
        if (screen instanceof MerchantScreen merchantScreen) {
            var midX = merchantScreen.width / 2;
            var baseY = merchantScreen.topPos + 174;
            var minecraft = Minecraft.getInstance();
            updateActiveTasks();

            screen.addRenderableWidget(new Buttons.AvailableTasksButton(
                midX - (Buttons.AvailableTasksButton.WIDTH / 2),
                baseY,
                b -> {
                    merchantScreen.onClose();
                    minecraft.setScreen(new AvailableTasksScreen());
                }));
        }

        if (screen instanceof InventoryScreen inventoryScreen && !activeTasks.isEmpty()) {
            var midX = inventoryScreen.width / 2;
            var baseY = inventoryScreen.topPos + 174;
            var minecraft = Minecraft.getInstance();
            updateActiveTasks();

            screen.addRenderableWidget(new Buttons.ActiveTasksButton(
                midX - (Buttons.ActiveTasksButton.WIDTH / 2),
                baseY,
                b -> {
                    inventoryScreen.onClose();
                    minecraft.setScreen(new ActiveTasksScreen());
                }));
        }
    }

    public void handleReceiveActiveTasks(Player player, Networking.S2CSendActiveTasks payload) {
        var tasks = payload.tasks();
        this.activeTasks = tasks;
        onActiveTasksUpdate.values().forEach(c -> c.accept(tasks));

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

    public Tasks getActiveTasks() {
        return activeTasks;
    }

    public Tasks getAvailableTasks() {
        return availableTasks;
    }

    public UUID getLastVillagerInteraction() {
        return lastVillagerInteraction;
    }

    public boolean availableTasksAreValid() {
        return !availableTasks.isEmpty() && availableTasks.uuid().equals(getLastVillagerInteraction());
    }

    public void acceptTask(Task task) {
        Networking.C2SQueryTask.send(TaskQuery.Accept, task.id);
    }

    public void abandonTask(Task task) {
        Networking.C2SQueryTask.send(TaskQuery.Abandon, task.id);
    }

    public void updateActiveTasks() {
        Networking.C2SRequestActiveTasks.send();
    }
}
