package svenhjol.charmony.tweaks.client.features.mob_textures.custom_renderers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SnowGolemRenderer;
import net.minecraft.client.renderer.entity.state.SnowGolemRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.SnowGolem;

import javax.annotation.Nullable;
import java.util.UUID;

public class CustomSnowGolemRenderer extends SnowGolemRenderer implements CustomRenderer {
    private UUID uuid;

    public CustomSnowGolemRenderer(EntityRendererProvider.Context context) {
        super(context);
        handlers.fillLayersFromOld(context, this, EntityType.SNOW_GOLEM);
    }

    @Override
    @Nullable
    public ResourceLocation getTextureLocation(SnowGolemRenderState livingEntityRenderState) {
        return handlers.texture(uuid, registers.snowGolems);
    }

    @Override
    public void extractRenderState(SnowGolem snowGolem, SnowGolemRenderState livingEntityRenderState, float f) {
        super.extractRenderState(snowGolem, livingEntityRenderState, f);
        this.uuid = snowGolem.getUUID();
    }
}
