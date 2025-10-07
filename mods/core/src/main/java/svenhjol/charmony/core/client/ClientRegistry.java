package svenhjol.charmony.core.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry.TexturedModelDataProvider;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.common.ContainerMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class ClientRegistry {
    public static final List<DeferredParticle> PARTICLES = new ArrayList<>();
    private final SidedFeature feature;

    private ClientRegistry(SidedFeature feature) {
        this.feature = feature;
    }

    public static ClientRegistry forFeature(SidedFeature feature) {
        return new ClientRegistry(feature);
    }

    public <E extends BlockEntity, S extends BlockEntityRenderState> Registerable<Void> blockEntityRenderer(BlockEntityType<E> blockEntityType, BlockEntityRendererProvider<E, S> provider) {
        return new Registerable<>(feature, () -> {
            BlockEntityRenderers.register(blockEntityType, provider);
            return null;
        });
    }

    public <B extends Block> Registerable<Void> blockRenderType(B block, ChunkSectionLayer chunkSectionLayer) {
        return new Registerable<>(feature, () -> {
            BlockRenderLayerMap.putBlock(block, chunkSectionLayer);
            return null;
        });
    }

    public void blockColor(BlockColor blockColor, List<? extends Block> blocks) {
        ColorProviderRegistry.BLOCK.register(blockColor, blocks.stream().toList().toArray(Block[]::new));
    }

    public <E extends Entity> Registerable<Void> entityRenderer(EntityType<? extends E> entity, EntityRendererProvider<E> provider) {
        return new Registerable<>(feature, () -> {
            EntityRenderers.register(entity, provider);
            return null;
        });
    }

    /**
     * May be run late. Use this to conditionally add and item to the creative menu if the feature is enabled.
     */
    public <I extends ItemLike> void itemTab(I item, ResourceKey<CreativeModeTab> key, ItemLike showAfter) {
        if (showAfter != null) {
            ItemGroupEvents.modifyEntriesEvent(key)
                .register(entries -> entries.addAfter(showAfter, item));
        } else {
            ItemGroupEvents.modifyEntriesEvent(key)
                .register(entries -> entries.accept(item));
        }
    }

    public <M extends ContainerMenu, S extends Screen & MenuAccess<M>> Registerable<?> menuScreen(MenuType<M> menu,
                                                                                                  MenuScreens.ScreenConstructor<M, S> screen) {
        return new Registerable<>(feature, () -> {
            MenuScreens.register(menu, screen);
            return null;
        });
    }

    public Registerable<ModelLayerLocation> modelLayer(ModelLayerLocation location, TexturedModelDataProvider dataProvider) {
        return new Registerable<>(feature, () -> {
            EntityModelLayerRegistry.registerModelLayer(location, dataProvider);
            return location;
        });
    }

    /**
     * Register a callback when the client receives a packet from the server.
     * @param type Packet type.
     * @param handler Callback.
     * @return Empty registerable.
     * @param <P> Payload class.
     */
    public <P extends CustomPacketPayload> Registerable<Void> packetReceiver(CustomPacketPayload.Type<P> type, BiConsumer<Player, P> handler) {
        return new Registerable<>(feature, () -> {
            ClientPlayNetworking.registerGlobalReceiver(type,
                (payload, context) -> context.client().execute(
                    () -> handler.accept(context.player(), payload)));
            return null;
        });
    }

    public DeferredParticle particle(SimpleParticleType type, ParticleResources.SpriteParticleRegistration<SimpleParticleType> registration) {
        var deferred = new DeferredParticle(type, registration);
        PARTICLES.add(deferred);
        return deferred;
    }
}
