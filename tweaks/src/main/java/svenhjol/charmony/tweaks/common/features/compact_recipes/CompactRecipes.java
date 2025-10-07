package svenhjol.charmony.tweaks.common.features.compact_recipes;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, canBeDisabled = false, description = """
    Adds some compact versions of vanilla recipes.""")
public class CompactRecipes extends SidedFeature {
    public CompactRecipes(Mod mod) {
        super(mod);
    }
}
