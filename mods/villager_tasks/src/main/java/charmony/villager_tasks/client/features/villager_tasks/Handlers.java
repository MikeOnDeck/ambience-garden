package charmony.villager_tasks.client.features.villager_tasks;

import charmony.core.base.Setup;
import charmony.villager_tasks.client.features.villager_tasks.screens.ActiveTasksScreen;
import charmony.villager_tasks.client.features.villager_tasks.screens.AvailableTasksScreen;
import charmony.villager_tasks.client.features.villager_tasks.screens.CompleteTasksScreen;
import charmony.villager_tasks.common.features.villager_tasks.Helpers;
import charmony.villager_tasks.common.features.villager_tasks.Networking;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.Tasks;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskQuery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class Handlers extends Setup<VillagerTasks> {
    private Tasks activeTasks = Tasks.EMPTY;
    private Tasks availableTasks = Tasks.EMPTY;
    private UUID lastVillagerInteraction = Helpers.emptyUuid();
    private Runnable runAfterUpdate = () -> {};

    public Handlers(VillagerTasks feature) {
        super(feature);
    }

    public void clientTick(Minecraft minecraft) {
        if (minecraft != null && minecraft.player instanceof Player player) {
            activeTasks.tasks().forEach(task -> task.onTick(task, player));

            // Villagers who own completed tasks will show particles.
            if (player.level().getGameTime() % 15 == 0) {
                highlightTaskOwners();
            }
        }
    }

    public void setupScreen(Screen screen) {
        if (screen instanceof MerchantScreen merchantScreen) {
            var midX = merchantScreen.width / 2;
            var top = merchantScreen.topPos + 174;
            var minecraft = Minecraft.getInstance();
            updateActiveTasks();

            // Tick the active tasks to ensure we have the latest status.
            clientTick(minecraft);

            var shouldShowAvailableButton = !availableTasks.isEmpty();
            var shouldShowCompleteButton = activeTasks.tasks().stream().anyMatch(Task::isSatisfied);

            var availableTasksX = shouldShowCompleteButton
                ? midX - 5 - (Buttons.AvailableTasksButton.WIDTH)
                : midX - (Buttons.AvailableTasksButton.WIDTH / 2);

            var completeTasksX = shouldShowAvailableButton
                ? midX + 5
                : midX - (Buttons.CompleteTasksButton.WIDTH / 2);

            if (shouldShowAvailableButton) {
                screen.addRenderableWidget(new Buttons.AvailableTasksButton(availableTasksX, top,
                    b -> {
                        merchantScreen.onClose();
                        minecraft.setScreen(new AvailableTasksScreen());
                    }));
            }

            if (shouldShowCompleteButton) {
                screen.addRenderableWidget(new Buttons.CompleteTasksButton(completeTasksX, top,
                    b -> {
                        merchantScreen.onClose();
                        minecraft.setScreen(new CompleteTasksScreen());
                    }));
            }
        }

        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            int inventoryTop;

            if (screen instanceof InventoryScreen) {
                inventoryTop = containerScreen.topPos + 174;
            } else if (screen instanceof CreativeModeInventoryScreen) {
                inventoryTop = containerScreen.topPos + 164;
            } else {
                return;
            }

            var midX = containerScreen.width / 2;
            var minecraft = Minecraft.getInstance();
            updateActiveTasks();

            if (!activeTasks.isEmpty()) {
                screen.addRenderableWidget(new Buttons.ActiveTasksButton(
                    midX - (Buttons.ActiveTasksButton.WIDTH / 2),
                    inventoryTop,
                    b -> {
                        containerScreen.onClose();
                        minecraft.setScreen(new ActiveTasksScreen());
                    }));
            }
        }
    }

    public void handleReceiveActiveTasks(Player player, Networking.S2CSendActiveTasks payload) {
        var tasks = payload.tasks();
        this.activeTasks = tasks;
        clientTick(Minecraft.getInstance());

        // Run anything queued for after the update.
        runAfterUpdate.run();

        // Clear the queued action.
        runAfterUpdate = () -> {};

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

    public void clearLastVillagerInteraction() {
        lastVillagerInteraction = Helpers.emptyUuid();
    }

    /**
     * Show particles above villagers who own completed tasks.
     */
    public void highlightTaskOwners() {
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        if (activeTasks.isEmpty()) {
            return;
        }

        var satisfied = activeTasks.tasks().stream().filter(Task::isSatisfied).toList();
        if (satisfied.isEmpty()) {
            return;
        }

        for (var task : satisfied) {
            Helpers.getNearbyTaskOwner(player, task.villager).ifPresent(villager -> {
                var spread = 0.75d;
                var villagerPos = villager.position();
                for (int i = 0; i < 3; i++) {
                    var px = villagerPos.x() + (Math.random() - 0.5d) * spread;
                    var py = villagerPos.y() + 2.25d + (Math.random() - 0.5d) * spread;
                    var pz = villagerPos.z() + (Math.random() - 0.5d) * spread;
                    player.level().addParticle(ParticleTypes.HAPPY_VILLAGER, px, py, pz, 0, 0, 0.12d);
                }
            });
        }
    }

    public boolean availableTasksAreValid() {
        return !availableTasks.isEmpty() && availableTasks.uuid().equals(getLastVillagerInteraction());
    }

    public void acceptTask(Task task, Runnable then) {
        this.runAfterUpdate = then;
        Networking.C2SQueryTask.send(TaskQuery.Accept, task.id);
    }

    public void abandonTask(Task task, Runnable then) {
        this.runAfterUpdate = then;
        Networking.C2SQueryTask.send(TaskQuery.Abandon, task.id);
    }

    public void completeTask(Task task, Runnable then) {
        this.runAfterUpdate = then;
        Networking.C2SQueryTask.send(TaskQuery.Complete, task.id);
    }

    public void updateActiveTasks() {
        Networking.C2SRequestActiveTasks.send();
    }

    public void updateAvailableTasks() {
        Networking.C2SRequestAvailableTasks.send(lastVillagerInteraction);
    }
}
