package charmony.tweaks.common.features.chiseled_bookshelves_show_book;

import charmony.api.core.FeatureDefinition;
import charmony.api.core.Side;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, description = """
    Shows the name and type of book when looking at a slot of a chiseled bookshelf.""")
public class ChiseledBookshelvesShowBook extends SidedFeature {
    public ChiseledBookshelvesShowBook(Mod mod) {
        super(mod);
    }
}
