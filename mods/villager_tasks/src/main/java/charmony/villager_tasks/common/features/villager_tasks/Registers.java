package charmony.villager_tasks.common.features.villager_tasks;

import charmony.api.core.Side;
import charmony.api.events.PlayerTickCallback;
import charmony.core.base.Setup;
import charmony.core.common.CommonRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class Registers extends Setup<VillagerTasks> {
    public final Supplier<SoundEvent> taskAbandon;
    public final Supplier<SoundEvent> taskAccept;
    public final Supplier<SoundEvent> taskComplete;
    public final Supplier<SoundEvent> taskEpicComplete;

    public Registers(VillagerTasks feature) {
        super(feature);
        var registry = CommonRegistry.forFeature(feature);

        // Packet registration.
        registry.packetSender(Side.Common, Networking.S2CSendActiveTasks.TYPE, Networking.S2CSendActiveTasks.CODEC);
        registry.packetSender(Side.Common, Networking.S2CSendAvailableTasks.TYPE, Networking.S2CSendAvailableTasks.CODEC);
        registry.packetSender(Side.Common, Networking.S2CSendVillagerInteraction.TYPE, Networking.S2CSendVillagerInteraction.CODEC);
        registry.packetSender(Side.Client, Networking.C2SQueryTask.TYPE, Networking.C2SQueryTask.CODEC);
        registry.packetSender(Side.Client, Networking.C2SRequestActiveTasks.TYPE, Networking.C2SRequestActiveTasks.CODEC);
        registry.packetSender(Side.Client, Networking.C2SRequestAvailableTasks.TYPE, Networking.C2SRequestAvailableTasks.CODEC);

        // Packet handling from client.
        registry.packetReceiver(Networking.C2SQueryTask.TYPE, feature.handlers::handleReceiveQueryTask);
        registry.packetReceiver(Networking.C2SRequestActiveTasks.TYPE, feature.handlers::handleReceiveRequestActiveTasks);
        registry.packetReceiver(Networking.C2SRequestAvailableTasks.TYPE, feature.handlers::handleReceiveRequestAvailableTasks);

        // Sound effects.
        taskAbandon = registry.sound("task_abandon");
        taskAccept = registry.sound("task_accept");
        taskComplete = registry.sound("task_complete");
        taskEpicComplete = registry.sound("task_epic_complete");
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
            UseEntityCallback.EVENT.register(feature().handlers::handleUseEntity);
        };
    }
}
