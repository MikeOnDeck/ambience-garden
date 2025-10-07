package svenhjol.charmony.core.common.features.conditional_recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import svenhjol.charmony.core.Charmony;
import svenhjol.charmony.core.base.Log;
import svenhjol.charmony.core.common.CommonRegistry;

import java.util.*;

/**
 * We create and register a resource reloader to parse conditional recipes.
 * Unfortunately, core Minecraft ignores the recipe map in the apply() method.
 * To work around this:
 * - add our conditional recipes to the map of all registered recipes
 * - create a new recipe map from the resulting map
 * - use a mixin in RecipeManager#finalizeRecipeLoading to load the new recipe map
 */
public class ConditionalRecipeManager extends SimpleJsonResourceReloadListener<Recipe<?>> implements IdentifiableResourceReloadListener {
    public static final ResourceLocation ID = Charmony.id("charmony_conditional_recipe_manager");
    protected static final Log LOGGER = new Log(Charmony.ID, "ConditionalRecipeManager");

    protected final DynamicOps<JsonElement> dynamicOps;
    protected static RecipeMap recipeMap;

    public ConditionalRecipeManager(HolderLookup.Provider provider) {
        super(provider, Recipe.CODEC, Registries.RECIPE);
        this.dynamicOps = provider.createSerializationContext(JsonOps.INSTANCE);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    protected void apply(Map<ResourceLocation, Recipe<?>> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        SortedMap<ResourceLocation, Recipe<?>> sortedMap = new TreeMap<>();

        for (var conditional : CommonRegistry.CONDITIONAL_RECIPES) {
            var id = conditional.id();
            conditional.create().ifPresent(jsonRecipe -> {
                var json = JsonParser.parseString(jsonRecipe.toString());
                Recipe.CODEC.parse(dynamicOps, json).ifSuccess(recipe -> {
                    if (sortedMap.putIfAbsent(id, recipe) != null) {
                        throw new IllegalStateException("Duplicate recipe ignored with ID " + id);
                    }
                    LOGGER.debug("Adding conditional recipe " + id.toString());
                }).ifError(error -> LOGGER.error("Couldn't parse recipe " + id + ": " + error));
            });
        }

        map.putAll(sortedMap);

        List<RecipeHolder<?>> list = new ArrayList<>(map.size());
        map.forEach((id, recipe) -> {
            var resourceKey = ResourceKey.create(Registries.RECIPE, id);
            var recipeHolder = new RecipeHolder<>(resourceKey, recipe);
            list.add(recipeHolder);
        });

        // This is a map of all vanilla recipes + our conditional recipes.
        recipeMap = RecipeMap.create(list);
    }

    public static Optional<RecipeMap> recipeMap() {
        return Optional.ofNullable(recipeMap);
    }
}
