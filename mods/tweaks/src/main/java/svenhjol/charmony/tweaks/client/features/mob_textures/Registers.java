package svenhjol.charmony.tweaks.client.features.mob_textures;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.client.ClientRegistry;
import svenhjol.charmony.tweaks.client.features.mob_textures.custom_renderers.CustomSnowGolemRenderer;
import svenhjol.charmony.tweaks.client.features.mob_textures.custom_renderers.CustomWanderingTraderRenderer;

import java.util.ArrayList;
import java.util.List;

public class Registers extends Setup<MobTextures> {
    public final List<ResourceLocation> snowGolems = new ArrayList<>();
    public final List<ResourceLocation> wanderingTraders = new ArrayList<>();

    public Registers(MobTextures feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            var registry = ClientRegistry.forFeature(feature());

            if (feature().snowGolems()) {
                registry.entityRenderer(EntityType.SNOW_GOLEM, CustomSnowGolemRenderer::new);
            }
            if (feature().wanderingTraders()) {
                registry.entityRenderer(EntityType.WANDERING_TRADER, CustomWanderingTraderRenderer::new);
            }

            ClientEntityEvents.ENTITY_LOAD.register(this::clientEntityLoad);
        };
    }

    private void clientEntityLoad(Entity entity, ClientLevel level) {
        if (!(entity instanceof Player)) return;
        setupCustomTextures(snowGolems, MobType.SNOW_GOLEM, "snow_golem", 5, true);
        setupCustomTextures(wanderingTraders, MobType.WANDERING_TRADER, "wandering_trader", 4, true);
    }

    @SuppressWarnings("SameParameterValue")
    private void setupCustomTextures(List<ResourceLocation> set, MobType mobType, String baseName, int max, boolean withVanilla) {
        set.clear();

        if (withVanilla) {
            set.add(mobType.vanillaTexture());
        }

        for (var i = 1; i <= max; i++) {
            set.add(mobType.customTexture(baseName + i));
        }
    }
}
