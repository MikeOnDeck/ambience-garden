package charmony.tweaks.common.features.compact_recipes;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, canBeDisabled = false, description = """
    Adds some compact versions of vanilla recipes.""")
public class CompactRecipes extends SidedFeature {
    public CompactRecipes(Mod mod) {
        super(mod);
    }
}
