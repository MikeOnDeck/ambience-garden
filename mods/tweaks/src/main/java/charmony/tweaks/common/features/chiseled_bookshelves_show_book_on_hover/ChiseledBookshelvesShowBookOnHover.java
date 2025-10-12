package charmony.tweaks.common.features.chiseled_bookshelves_show_book_on_hover;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = """
    Shows the name and type of book when looking at a slot of a chiseled bookshelf.""")
public class ChiseledBookshelvesShowBookOnHover extends SidedFeature {
    public ChiseledBookshelvesShowBookOnHover(Mod mod) {
        super(mod);
    }
}
