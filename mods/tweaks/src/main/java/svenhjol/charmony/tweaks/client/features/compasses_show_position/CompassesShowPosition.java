package svenhjol.charmony.tweaks.client.features.compasses_show_position;

import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, description = """
    Shows the direction that the player is facing, the position coordinates and current biome when holding a compass.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class CompassesShowPosition extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    @Configurable(
        name = "Show facing",
        description = "If true, shows the cardinal direction that the player is facing.",
        requireRestart = false
    )
    private static boolean showFacing = true;

    @Configurable(
        name = "Show co-ordinates",
        description = "If true, shows the player's XYZ coordinates.",
        requireRestart = false
    )
    private static boolean showCoords = true;

    @Configurable(
        name = "Show biome",
        description = "If true, shows the player's biome.",
        requireRestart = false
    )
    private static boolean showBiome = true;

    @Configurable(
        name = "Only show X and Z",
        description = "If true, only show the player's X and Z coordinates (not their height/depth).",
        requireRestart = false
    )
    private static boolean onlyShowXZ = false;

    @Configurable(
        name = "Only show when sneaking",
        description = "If true, only show the compass overlay if the player is sneaking.",
        requireRestart = false
    )
    private static boolean onlyShowWhenSneaking = false;

    @Configurable(
        name = "Always show",
        description = "If true, the overlay will always be shown even if the player is not holding a compass.",
        requireRestart = false
    )
    private static boolean alwaysShow = false;

    public CompassesShowPosition(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
    }

    public boolean showFacing() {
        return showFacing;
    }

    public boolean showCoords() {
        return showCoords;
    }

    public boolean showBiome() {
        return showBiome;
    }

    public boolean onlyShowXZ() {
        return onlyShowXZ;
    }

    public boolean onlyShowWhenSneaking() {
        return onlyShowWhenSneaking;
    }

    public boolean alwaysShow() {
        return alwaysShow;
    }
}
