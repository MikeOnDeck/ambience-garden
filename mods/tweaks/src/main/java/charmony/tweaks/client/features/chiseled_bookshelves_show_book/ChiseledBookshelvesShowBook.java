package charmony.tweaks.client.features.chiseled_bookshelves_show_book;

import charmony.api.core.Configurable;
import charmony.api.core.FeatureDefinition;
import charmony.api.core.Side;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import net.minecraft.util.Mth;

@FeatureDefinition(side = Side.Client, canBeDisabledInConfig = false)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class ChiseledBookshelvesShowBook extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    @Configurable(
        name = "Offset from center",
        description = "Number of pixels below the center of the screen at which the display text will be rendered.",
        requireRestart = false
    )
    private static int offsetFromCenter = 20;

    public ChiseledBookshelvesShowBook(Mod mod) {
        super(mod);
        this.registers = new Registers(this);
        this.handlers = new Handlers(this);
    }

    public static ChiseledBookshelvesShowBook feature() {
        return Mod.getSidedFeature(ChiseledBookshelvesShowBook.class);
    }

    public int offsetFromCenter() {
        return Mth.clamp(offsetFromCenter, -1024, 1024);
    }
}
