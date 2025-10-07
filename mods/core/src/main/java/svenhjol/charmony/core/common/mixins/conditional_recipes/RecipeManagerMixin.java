package svenhjol.charmony.core.common.mixins.conditional_recipes;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.core.common.features.conditional_recipes.ConditionalRecipes;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Shadow @Mutable private RecipeMap recipes;

    /**
     * Load our custom recipe map object when Minecraft is ready to process them.
     * Our custom map contains all default and modded recipes with our conditional recipes appended to it.
     * ConditionalRecipes#recipeMap will return an empty optional if the feature is disabled.
     */
    @Inject(
        method = "finalizeRecipeLoading",
        at = @At("HEAD")
    )
    private void hookFinalizeRecipeLoading(FeatureFlagSet featureFlagSet, CallbackInfo ci) {
        ConditionalRecipes.feature().recipeMap().ifPresent(map -> this.recipes = map);
    }
}
