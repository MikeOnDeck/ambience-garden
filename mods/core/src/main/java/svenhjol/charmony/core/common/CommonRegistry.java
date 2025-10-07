package svenhjol.charmony.core.common;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import org.apache.commons.lang3.tuple.Pair;
import svenhjol.charmony.api.core.FuelProvider;
import svenhjol.charmony.api.core.IgniteProvider;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.common.dispenser.ConditionalDispenseItemBehavior;
import svenhjol.charmony.core.common.features.conditional_recipes.ConditionalRecipe;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;
import svenhjol.charmony.core.helpers.VillagerHelper;

import java.util.*;
import java.util.function.*;

import static net.minecraft.world.entity.npc.VillagerTrades.WANDERING_TRADER_TRADES;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public final class CommonRegistry {
    private final SidedFeature feature;

    public static final Map<Mod, List<PotionRecipe>> POTION_RECIPES = new HashMap<>();
    public static final List<ConditionalRecipe> CONDITIONAL_RECIPES = new ArrayList<>();
    public static final Map<ItemLike, List<ConditionalDispenseItemBehavior>> CONDITIONAL_DISPENSER_BEHAVIORS = new HashMap<>();
    public static final List<DataComponentType<? extends TooltipProvider>> DATA_COMPONENT_TOOLTIP_PROVIDERS = new ArrayList<>();

    private CommonRegistry(SidedFeature feature) {
        this.feature = feature;
    }

    /**
     * Call this after mod feature registration has been completed so that registry events can be handled for the mod.
     */
    public static void finishModRegistration(Mod mod) {
        // Register all the potion recipes for the mod.
        var potionRecipes = POTION_RECIPES.getOrDefault(mod, List.of());
        FabricBrewingRecipeRegistryBuilder.BUILD.register(
            builder -> potionRecipes.forEach(
                recipe -> builder.addMix(recipe.input(), recipe.reagent().get(), recipe.output())));
    }

    public static CommonRegistry forFeature(SidedFeature feature) {
        return new CommonRegistry(feature);
    }

    public Registerable<Holder<Attribute>> attribute(String id, Supplier<Attribute> supplier) {
        return new Registerable<>(feature,
            () -> Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, feature.registryId(id), supplier.get()));
    }

    public <E extends Entity> Registerable<Void> biomeSpawn(Predicate<Holder<Biome>> predicate, MobCategory category,
                                                            Supplier<EntityType<E>> entity, int weight, int minGroupSize, int maxGroupSize) {
        return new Registerable<>(feature, () -> {
            Predicate<BiomeSelectionContext> biomeSelectionContext = c -> predicate.test(c.getBiomeRegistryEntry());
            BiomeModifications.addSpawn(biomeSelectionContext, category, entity.get(), weight, minGroupSize, maxGroupSize);
            return null;
        });
    }

    public <B extends Block> Registerable<B> block(String id, Function<ResourceKey<Block>, B> funcSupplier) {
        return new Registerable<>(feature, () -> {
            var res = feature.registryId(id);
            var key = ResourceKey.create(Registries.BLOCK, res);
            return Registry.register(BuiltInRegistries.BLOCK, res, funcSupplier.apply(key));
        });
    }

    public <BE extends BlockEntity, B extends Block> Registerable<BlockEntityType<BE>> blockEntity(String id,
                                                                                               Supplier<FabricBlockEntityTypeBuilder.Factory<BE>> builder,
                                                                                               List<Supplier<B>> blocks) {
        return new Registerable<>(feature, () -> {
            var blocksToAdd = blocks.stream().map(Supplier::get).toArray(Block[]::new);
            return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, feature.registryId(id),
                FabricBlockEntityTypeBuilder.create(builder.get(), blocksToAdd).build());
        });
    }

    public <BE extends BlockEntity> Registerable<BlockEntityType<BE>> blockEntity(String id, Supplier<FabricBlockEntityTypeBuilder.Factory<BE>> builder) {
        return blockEntity(id, builder, List.of());
    }

    public Registerable<BlockSetType> blockSetType(Supplier<WoodMaterial> material) {
        return new Registerable<>(feature, () -> {
            var materialName = material.get().getSerializedName();
            return BlockSetType.register(new BlockSetType(materialName));
        });
    }

    /**
     * May be run late. Use this to conditionally add blocks to a block entity if the feature is enabled.
     */
    public <BE extends BlockEntity> void blocksForBlockEntity(Supplier<BlockEntityType<BE>> supplier, List<Supplier<? extends Block>> blocks) {
        var blockEntity = supplier.get();
        var blockEntityBlocks = blockEntity.validBlocks;
        List<Block> mutable = new ArrayList<>(blockEntityBlocks);

        for (Supplier<? extends Block> blockSupplier : blocks) {
            var block = blockSupplier.get();
            if (!mutable.contains(block)) {
                mutable.add(block);
            }
        }

        blockEntity.validBlocks = new HashSet<>(mutable);
    }

    public <I extends ItemLike, C extends ConditionalDispenseItemBehavior> Registerable<Void> conditionalDispenserBehavior(Supplier<I> itemSupplier, Supplier<C> behaviorSupplier) {
        return new Registerable<>(feature, () -> {
            var item = itemSupplier.get();
            var behavior = behaviorSupplier.get();
            CONDITIONAL_DISPENSER_BEHAVIORS.computeIfAbsent(item, a -> new ArrayList<>()).add(behavior);
            return null;
        });
    }

    /**
     * May be run late. Use this to add a recipe that varies according to mod state.
     *
     * @param recipe Conditional recipe to add.
     */
    public void conditionalRecipe(ConditionalRecipe recipe) {
        CONDITIONAL_RECIPES.add(recipe);
    }

    public <D> Registerable<DataComponentType<D>> dataComponent(String id, Supplier<UnaryOperator<DataComponentType.Builder<D>>> dataComponent) {
        return new Registerable<>(feature, () -> DataComponents.register(feature.registryId(id).toString(), dataComponent.get()));
    }

    public <T extends TooltipProvider> Registerable<Void> dataComponentTooltipProvider(Supplier<DataComponentType<T>> dataComponentType) {
        return new Registerable<>(feature, () -> {
            DATA_COMPONENT_TOOLTIP_PROVIDERS.add(dataComponentType.get());
            return null;
        });
    }

    public <I extends ItemLike, D extends DispenseItemBehavior> Registerable<Void> dispenserBehavior(Supplier<I> item, Supplier<D> dispenserBehavior) {
        return new Registerable<>(feature, () -> {
            var behavior = dispenserBehavior.get();
            DispenserBlock.registerBehavior(item.get(), behavior);
            return null;
        });
    }

    public ResourceKey<Enchantment> enchantment(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, feature.registryId(name));
    }

    public <E extends Entity> Registerable<EntityType<E>> entity(String id, Supplier<EntityType.Builder<E>> supplier) {
        return new Registerable<>(feature, () -> {
            var res = feature.registryId(id);
            var key = ResourceKey.create(Registries.ENTITY_TYPE, res);
            return Registry.register(BuiltInRegistries.ENTITY_TYPE, feature.registryId(id), supplier.get().build(key));
        });
    }

    public <T extends LivingEntity> Registerable<Void> entityAttribute(Supplier<EntityType<T>> entitySupplier, Supplier<Holder<Attribute>> attributeSupplier) {
        return new Registerable<>(feature, () -> {
            // Unwrap suppliers
            var entity = entitySupplier.get();
            var attribute = attributeSupplier.get();

            var attributes = DefaultAttributes.getSupplier(entity);
            if (!(attributes.instances instanceof LinkedHashMap<Holder<Attribute>, AttributeInstance>)) {
                // Make mutable so we can add the new one.
                attributes.instances = new LinkedHashMap<>(attributes.instances);
            }

            if (!attributes.hasAttribute(attribute)) {
                var instance = new AttributeInstance(attribute, x -> {});
                attributes.instances.put(attribute, instance);
            }
            return null;
        });
    }

    public SidedFeature feature() {
        return feature;
    }

    public <T extends FuelProvider> Registerable<Void> fuel(Supplier<T> provider) {
        return new Registerable<>(feature, () -> {
            var item = provider.get();
            FuelRegistryEvents.BUILD.register(
                (builder, ctx) -> builder.add((ItemLike)item, item.fuelTime()));
            return null;
        });
    }

    public <T extends IgniteProvider> Registerable<Void> ignite(Supplier<T> provider) {
        return new Registerable<>(feature, () -> {
            var block = provider.get();
            ((FireBlock) Blocks.FIRE).setFlammable((Block)block, block.igniteChance(), block.burnChance());
            return null;
        });
    }

    public <I extends Item> Registerable<I> item(String id, Function<ResourceKey<Item>, I> funcSupplier) {
        return new Registerable<>(feature, () -> {
            var res = feature.registryId(id);
            var key = ResourceKey.create(Registries.ITEM, res);
            return Registry.register(BuiltInRegistries.ITEM, feature.registryId(id), funcSupplier.apply(key));
        });
    }

    public <T extends LootItemFunctionType<? extends LootItemConditionalFunction>> Registerable<T> lootFunctionType(String id, Supplier<T> supplier) {
        return new Registerable<>(feature, () -> Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE,
            feature.registryId(id), supplier.get()));
    }

    public <T extends MenuType<M>, M extends AbstractContainerMenu> Registerable<T> menuType(String id, Supplier<T> menuSupplier) {
        return new Registerable<>(feature,
            () -> Registry.register(BuiltInRegistries.MENU, feature.registryId(id), menuSupplier.get()));
    }

    /**
     * This sets all the attributes for a mob. Use it when creating custom mobs.
     * Don't do this for vanilla mobs; use mobAttribute() to add new attributes.
     */
    public <M extends Mob> Registerable<Void> mobAttributes(Supplier<EntityType<M>> mob, Supplier<AttributeSupplier.Builder> builder) {
        return new Registerable<>(feature, () -> {
            FabricDefaultAttributeRegistry.register(mob.get(), builder.get());
            return null;
        });
    }

    public <M extends Mob> Registerable<Void> mobSpawnPlacement(Supplier<EntityType<M>> mob, SpawnPlacementType placementType,
                                                                Heightmap.Types heightmapType, SpawnPlacements.SpawnPredicate<M> predicate) {
        return new Registerable<>(feature, () -> {
            SpawnPlacements.register(mob.get(), placementType, heightmapType, predicate);
            return null;
        });
    }

    /**
     * Register a callback when the server receives a packet from the client.
     *
     * @param type Packet type.
     * @param handler Callback.
     * @return Empty registerable.
     * @param <P> Payload class.
     */
    public <P extends CustomPacketPayload> Registerable<Void> packetReceiver(CustomPacketPayload.Type<P> type, Supplier<BiConsumer<Player, P>> handler) {
        return new Registerable<>(feature, () -> {
            ServerPlayNetworking.registerGlobalReceiver(type,
                (payload, context) -> {
                var player = context.player();
                var server = context.server();
                if (server != null) {
                    server.execute(() -> handler.get().accept(player, payload));
                }
            });
            return null;
        });
    }

    /**
     * Register packet to send from the specified side.
     *
     * @param side Side to send the packet from.
     * @param type Packet type.
     * @param codec Packet codec.
     * @return Empty registerable.
     * @param <P> Payload class.
     */
    @SuppressWarnings("unchecked")
    public <P extends CustomPacketPayload> Registerable<Void> packetSender(Side side, CustomPacketPayload.Type<P> type, StreamCodec<? extends ByteBuf, P> codec) {
        return new Registerable<>(feature, () -> {
            switch (side) {
                case Common -> PayloadTypeRegistry.playS2C().register(type, (StreamCodec<? super ByteBuf, P>)codec);
                case Client -> PayloadTypeRegistry.playC2S().register(type, (StreamCodec<? super ByteBuf, P>)codec);
            }
            return null;
        });
    }

    public Registerable<Void> pointOfInterestBlockStates(Supplier<Holder<PoiType>> poiType, Supplier<List<BlockState>> states) {
        return new Registerable<>(feature, () -> {
            var resourceKey = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getResourceKey(poiType.get().value()).orElseThrow();
            var holder = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getOrThrow(resourceKey);
            var blockStates = new ArrayList<>(holder.value().matchingStates());
            blockStates.addAll(states.get());
            blockStates.forEach(state -> PoiTypes.TYPE_BY_STATE.put(state, holder));
            return null;
        });
    }

    public Registerable<Holder<Potion>> potion(String id, Supplier<Potion> supplier) {
        return new Registerable<>(feature,
            () -> Registry.registerForHolder(BuiltInRegistries.POTION, feature.registryId(id), supplier.get()));
    }

    public void potionRecipe(Holder<Potion> input, Supplier<Item> reagent, Holder<Potion> output) {
        POTION_RECIPES.computeIfAbsent(feature.mod(), l -> new ArrayList<>()).add(new PotionRecipe(input, reagent, output));
    }

    public Registerable<SoundEvent> sound(String id) {
        var res = feature.registryId(id);
        return new Registerable<>(feature, () -> Registry.register(BuiltInRegistries.SOUND_EVENT, res, SoundEvent.createVariableRangeEvent(res)));
    }

    public <B extends Block, S extends Block> Registerable<Void> strippable(Supplier<B> block, Supplier<S> strippedBlock) {
        return new Registerable<>(feature, () -> {
            AxeItem.STRIPPABLES = new HashMap<>(AxeItem.STRIPPABLES);
            AxeItem.STRIPPABLES.put(block.get(), strippedBlock.get());
            return null;
        });
    }

    public <S extends Structure> Supplier<StructureType<S>> structure(String id, Supplier<MapCodec<S>> codec) {
        return new Registerable<>(feature, () -> {
            var res = feature.registryId(id);
            return Registry.register(BuiltInRegistries.STRUCTURE_TYPE, res.toString(), codec::get);
        });
    }

    public Supplier<StructurePieceType> structurePiece(String id, Supplier<StructurePieceType> piece) {
        return new Registerable<>(feature, () -> {
            var res = feature.registryId(id);
            return Registry.register(BuiltInRegistries.STRUCTURE_PIECE, res, piece.get());
        });
    }

    /**
     * May be run late. Use this to conditionally add villager trades if the feature is enabled.
     */
    public Registerable<Void> villagerTrade(Supplier<ResourceKey<VillagerProfession>> profession, int tier, Supplier<VillagerTrades.ItemListing> trade) {
        return new Registerable<>(feature, () -> {
            var trades = VillagerHelper.getMutableTrades(profession.get());
            trades.get(tier).add(trade.get());
            VillagerHelper.reassembleTrades(profession.get(), trades);
            return null;
        });
    }

    /**
     * May be run late. Use this to conditionally add trades to a wandering trade if the feature is enabled.
     */
    public Registerable<Void> wandererTrade(Supplier<VillagerTrades.ItemListing> trade, boolean isRare) {
        return new Registerable<>(feature, () -> {
            WANDERING_TRADER_TRADES = new ArrayList<>(WANDERING_TRADER_TRADES);

            var size = WANDERING_TRADER_TRADES.size();
            var index = isRare ? 2 : 1;
            Pair<VillagerTrades.ItemListing[], Integer> tradeList;

            try {
                tradeList = WANDERING_TRADER_TRADES.get(index);
            } catch (IndexOutOfBoundsException e) {
                index = size - 1;
                tradeList = WANDERING_TRADER_TRADES.get(index);
            }

            var trades = new ArrayList<>(Arrays.asList(tradeList.getLeft()));
            var count = tradeList.getRight();
            trades.add(trade.get());

            // Rebuild the trades at this index.
            WANDERING_TRADER_TRADES.add(index, Pair.of(trades.toArray(new VillagerTrades.ItemListing[0]), count));
            return null;
        });
    }

    public Registerable<WoodType> woodType(String id, Supplier<BlockSetType> blockSetType) {
        return new Registerable<>(feature, () -> WoodType.register(new WoodType(id, blockSetType.get())));
    }
}
