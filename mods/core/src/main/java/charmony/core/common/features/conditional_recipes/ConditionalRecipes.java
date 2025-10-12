package charmony.core.common.features.conditional_recipes;

import net.minecraft.world.item.crafting.RecipeMap;
import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

import java.util.Optional;

@FeatureDefinition(side = Side.Common, description = """
    Conditional recipes for Charmony features.
    This manipulates vanilla recipe manager functionality. If you experience issues with recipes, try disabling this feature.
    If this feature is disabled, no Charmony conditional recipes will be loaded.""")
public final class ConditionalRecipes extends SidedFeature {
    public final Registers registers;

    public ConditionalRecipes(Mod mod) {
        super(mod);
        registers = new Registers(this);
    }

    public static ConditionalRecipes feature() {
        return Mod.getSidedFeature(ConditionalRecipes.class);
    }

    public Optional<RecipeMap> recipeMap() {
        if (!this.enabled()) {
            return Optional.empty();
        }
        return ConditionalRecipeManager.recipeMap();
    }
}
