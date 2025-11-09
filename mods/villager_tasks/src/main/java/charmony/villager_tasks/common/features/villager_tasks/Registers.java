package charmony.villager_tasks.common.features.villager_tasks;

import charmony.api.core.Side;
import charmony.api.events.ItemPickupCallback;
import charmony.api.events.PlayerTickCallback;
import charmony.core.base.Setup;
import charmony.core.common.CommonRegistry;
import charmony.villager_tasks.common.features.villager_tasks.requirements.TreasureLootFunction;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

import java.util.function.Supplier;

public class Registers extends Setup<VillagerTasks> {
    public final Supplier<SoundEvent> taskAbandon;
    public final Supplier<SoundEvent> taskAccept;
    public final Supplier<SoundEvent> taskCompleteSound;
    public final Supplier<SoundEvent> epicTaskCompleteSound;
    public final Supplier<LootItemFunctionType<TreasureLootFunction>> treasureLootFunction;

    public Registers(VillagerTasks feature) {
        super(feature);
        var registry = CommonRegistry.forFeature(feature);

        // Packet registration.
        registry.packetSender(Side.Common, Networking.S2CSendActiveTasks.TYPE, Networking.S2CSendActiveTasks.CODEC);
        registry.packetSender(Side.Common, Networking.S2CSendAvailableTasks.TYPE, Networking.S2CSendAvailableTasks.CODEC);
        registry.packetSender(Side.Common, Networking.S2CSendVillagerInteraction.TYPE, Networking.S2CSendVillagerInteraction.CODEC);
        registry.packetSender(Side.Client, Networking.C2SQueryTask.TYPE, Networking.C2SQueryTask.CODEC);
        registry.packetSender(Side.Client, Networking.C2SRequestActiveTasks.TYPE, Networking.C2SRequestActiveTasks.CODEC);

        // Packet handling from client.
        registry.packetReceiver(Networking.C2SQueryTask.TYPE, feature.handlers::handleReceiveQueryTask);
        registry.packetReceiver(Networking.C2SRequestActiveTasks.TYPE, feature.handlers::handleReceiveRequestActiveTasks);

        // Sound effects.
        taskAbandon = registry.sound("task_abandon");
        taskAccept = registry.sound("task_accept");
        taskCompleteSound = registry.sound("task_complete");
        epicTaskCompleteSound = registry.sound("epic_task_complete");

        // Loot functions.
        treasureLootFunction = registry.lootFunctionType("treasure_loot_function", () -> TreasureLootFunction.CODEC);
    }

    @Override
    public Runnable boot() {
        return () -> {
            ServerWorldEvents.LOAD.register(((server, level) -> {
                // Load all definitions on world load. We need tags to already be resolved.
                feature().handlers.loadDefinitions(server);
            }));

            PlayerTickCallback.EVENT.register(feature().handlers::playerTick);
            ServerEntityEvents.ENTITY_LOAD.register(feature().handlers::entityJoin);
            UseEntityCallback.EVENT.register(feature().handlers::useEntity);
            ServerLivingEntityEvents.AFTER_DEATH.register(feature().handlers::afterEntityDeath);
            ItemPickupCallback.EVENT.register(feature().handlers::itemPickup);
            LootTableEvents.MODIFY.register(feature().handlers::lootTableModify);
        };
    }
}
