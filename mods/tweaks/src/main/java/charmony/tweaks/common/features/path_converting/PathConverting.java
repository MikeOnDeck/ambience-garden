package charmony.tweaks.common.features.path_converting;

import charmony.api.core.Configurable;
import charmony.api.core.FeatureDefinition;
import charmony.api.core.Side;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, description = "Use a hoe to convert path blocks back to dirt.")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class PathConverting extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;
    public final Advancements advancements;

    @Configurable(
        name = "Convert path to dirt",
        description = "If true, a hoe can be used to convert a path block to a dirt block."
    )
    private static boolean pathToDirt = true;

    public PathConverting(Mod mod) {
        super(mod);

        registers = new Registers(this);
        handlers = new Handlers(this);
        advancements = new Advancements(this);
    }

    public boolean allowPathToDirt() {
        return pathToDirt;
    }
}
