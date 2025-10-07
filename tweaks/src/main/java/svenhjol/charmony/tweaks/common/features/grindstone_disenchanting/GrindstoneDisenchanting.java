package svenhjol.charmony.tweaks.common.features.grindstone_disenchanting;

import net.minecraft.util.Mth;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = """
    Extract enchantments from any enchanted item onto an empty book using the grindstone.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class GrindstoneDisenchanting extends SidedFeature {
    public final Advancements advancements;
    public final Handlers handlers;
    public final Registers registers;

    @Configurable(name = "Initial cost", description = "Initial cost (in XP levels) of extraction before adding on the cost of the enchantment(s).")
    private static int initialCost = 5;

    @Configurable(name = "Treasure cost", description = "Adds extra cost (in XP levels) if the enchantment is a treasure enchantment such as Mending.")
    private static int treasureCost = 5;

    @Configurable(name = "Add item repair cost", description = "If true, the item's repair cost will be added to the cost of extraction.")
    private static boolean addRepairCost = true;

    public GrindstoneDisenchanting(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
        advancements = new Advancements(this);
    }

    public static GrindstoneDisenchanting feature() {
        return Mod.getSidedFeature(GrindstoneDisenchanting.class);
    }

    public int initialCost() {
        return Mth.clamp(initialCost, 0, 30);
    }

    public int treasureCost() {
        return Mth.clamp(treasureCost, 0, 30);
    }

    public boolean addRepairCost() {
        return addRepairCost;
    }
}