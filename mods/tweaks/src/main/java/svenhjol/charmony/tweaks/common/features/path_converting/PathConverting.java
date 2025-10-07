package svenhjol.charmony.tweaks.common.features.path_converting;

import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = "Use a shovel or hoe to convert dirt and path blocks.")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class PathConverting extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;
    public final Advancements advancements;

    @Configurable(
        name = "Convert dirt to path",
        description = "If true, a shovel can be used to convert a dirt block to a path block."
    )
    private static boolean dirtToPath = true;

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

    public boolean allowDirtToPath() {
        return dirtToPath;
    }

    public boolean allowPathToDirt() {
        return pathToDirt;
    }
}
