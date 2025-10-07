package svenhjol.charmony.tweaks.client.features.mob_textures.custom_renderers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.WanderingTrader;

import javax.annotation.Nullable;
import java.util.UUID;

public class CustomWanderingTraderRenderer extends WanderingTraderRenderer implements CustomRenderer {
    private UUID uuid;

    public CustomWanderingTraderRenderer(EntityRendererProvider.Context context) {
        super(context);
        handlers.fillLayersFromOld(context, this, EntityType.WANDERING_TRADER);
    }

    @Override
    @Nullable
    public ResourceLocation getTextureLocation(VillagerRenderState villagerRenderState) {
        return handlers.texture(uuid, registers.wanderingTraders);
    }

    @Override
    public void extractRenderState(WanderingTrader wanderingTrader, VillagerRenderState villagerRenderState, float f) {
        super.extractRenderState(wanderingTrader, villagerRenderState, f);
        this.uuid = wanderingTrader.getUUID();
    }
}
