package charmony.villager_tasks.common.features.villager_tasks;

import charmony.core.base.Setup;
import charmony.core.common.CommonRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class Registers extends Setup<VillagerTasks> {
    public static final String DEFINITIONS_DIR = "villager_tasks";

    public final List<Definition> definitions = new ArrayList<>();

    public Registers(VillagerTasks feature) {
        super(feature);
        var registry = CommonRegistry.forFeature(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            ServerWorldEvents.LOAD.register(((server, level) -> {
                // Load all definitions on world load.
                // We need to do this here to resolve all tags.
                loadDefinitions(server);
            }));
        };
    }

    private void loadDefinitions(MinecraftServer server) {
        definitions.clear();

        var manager = server.getResourceManager();
        var files = manager.listResources(DEFINITIONS_DIR, file -> file.getPath().endsWith(".json"));

        for (var file : files.entrySet()) {
            var id = file.getKey();
            var path = id.getPath();
            var resource = file.getValue();

            Definition def;

            try {
                def = Definition.fromJson(id, manager, resource);
            } catch (Exception e) {
                log().error("Failed to load villager task definition from " + id + ": " + e.getMessage());
                continue;
            }

            definitions.add(def);
            log().debug("Loaded villager task definition: " + path);
        }
    }
}
