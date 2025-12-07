package charmony.villager_tasks.client.features.villager_tasks;

import charmony.core.base.Setup;
import charmony.villager_tasks.client.features.villager_tasks.screens.ActiveTasksScreen;
import charmony.villager_tasks.client.features.villager_tasks.screens.AvailableTasksScreen;
import charmony.villager_tasks.client.features.villager_tasks.screens.BaseScreen;
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
    private Task pinnedTask = Task.EMPTY;

    public Handlers(VillagerTasks feature) {
        super(feature);
    }

    public void clientTick(Minecraft minecraft) {
        if (minecraft == null) return;

        if (minecraft.player instanceof Player player) {
            activeTasks.tasks().forEach(task -> task.onTick(task, player));

            // Villagers who own completed tasks will show particles.
            if (player.level().getGameTime() % 15 == 0) {
                highlightTaskOwners();
            }
        }

        // Check the pinned task to make sure that it's still valid.
        if (minecraft.level != null && minecraft.level.getGameTime() % 10 == 0
            && !pinnedTask.isEmpty() && !isPinnedTaskValid()) {
            clearPinnedTask();
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
        var minecraft = Minecraft.getInstance();
        this.activeTasks = tasks;

        clientTick(minecraft);
        refreshScreen(minecraft);

        log().info("Client received " + tasks.tasks().size() + " active tasks.");
    }

    public void handleReceiveAvailableTasks(Player player, Networking.S2CSendAvailableTasks payload) {
        var tasks = payload.tasks();
        var minecraft = Minecraft.getInstance();
        this.availableTasks = tasks;

        clientTick(minecraft);
        refreshScreen(minecraft);

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

    public void clearPinnedTask() {
        this.pinnedTask = Task.EMPTY;
    }

    public void setPinnedTask(Task task) {
        this.pinnedTask = task;
    }

    public boolean isPinnedTask(Task task) {
        return this.pinnedTask.id == task.id;
    }

    public boolean isPinnedTaskValid() {
        return pinnedTask.isStarted() && activeTasks.tasks().stream().anyMatch(t -> t.id == pinnedTask.id);
    }

    public void refreshScreen(Minecraft minecraft) {
        if (minecraft.screen instanceof BaseScreen baseScreen) {
            baseScreen.refresh();
        }
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

    public void acceptTask(Task task) {
        Networking.C2SQueryTask.send(TaskQuery.Accept, task.id);
    }

    public void abandonTask(Task task) {
        Networking.C2SQueryTask.send(TaskQuery.Abandon, task.id);
    }

    public void completeTask(Task task) {
        Networking.C2SQueryTask.send(TaskQuery.Complete, task.id);
    }

    public void updateActiveTasks() {
        Networking.C2SRequestActiveTasks.send();
    }
}
