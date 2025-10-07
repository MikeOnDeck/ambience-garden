package svenhjol.charmony.tweaks.client.features.mob_textures;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import svenhjol.charmony.core.base.Setup;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class Handlers extends Setup<MobTextures> {
    public Handlers(MobTextures feature) {
        super(feature);
    }

    @SuppressWarnings("unchecked")
    public <T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<S>, U extends S> void fillLayersFromOld(
        EntityRendererProvider.Context context, LivingEntityRenderer<T, U, M> renderer, EntityType<T> type) {
        EntityRenderer<?, ?> old = context.getEntityRenderDispatcher().renderers.get(type);

        if (old != null) {
            var layerRenderers = renderer.layers;
            layerRenderers.clear();

            ((LivingEntityRenderer<T, U, M>)old).layers
                .stream()
                .peek(layer -> layer.renderer = renderer)
                .forEach(layerRenderers::add);
        }
    }

    @Nullable
    public ResourceLocation texture(UUID uuid, List<ResourceLocation> textures) {
        if (textures.isEmpty()) {
            return null;
        }

        var choice = Math.abs((int)(uuid.getMostSignificantBits() % textures.size()));
        return textures.get(choice);
    }
}
