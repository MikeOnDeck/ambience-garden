package svenhjol.charmony.tweaks.client.features.chiseled_bookshelves_show_book_on_hover;

import net.minecraft.util.Mth;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, canBeDisabledInConfig = false)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class ChiseledBookshelvesShowBookOnHover extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    @Configurable(
        name = "Offset from center",
        description = "Number of pixels below the center of the screen at which the display text will be rendered.",
        requireRestart = false
    )
    private static int offsetFromCenter = 20;

    public ChiseledBookshelvesShowBookOnHover(Mod mod) {
        super(mod);
        this.registers = new Registers(this);
        this.handlers = new Handlers(this);
    }

    public static ChiseledBookshelvesShowBookOnHover feature() {
        return Mod.getSidedFeature(ChiseledBookshelvesShowBookOnHover.class);
    }

    public int offsetFromCenter() {
        return Mth.clamp(offsetFromCenter, -1024, 1024);
    }
}
