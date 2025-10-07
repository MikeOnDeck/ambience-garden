package svenhjol.charmony.core.common.features.conditional_recipes;

import net.minecraft.world.item.crafting.RecipeMap;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

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
