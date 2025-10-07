package svenhjol.charmony.ambient_sounds.client.features.environment;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

import java.util.ArrayList;
import java.util.List;

@FeatureDefinition(side = Side.Client, description = """
    Plays ambient sound according to the player's environment.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class Environment extends SidedFeature {
    private final List<ResourceLocation> validCaveDimensions = new ArrayList<>();
    public Registers registers;
    public Handlers handlers;

    @Configurable(name = "Alien sounds", requireRestart = false, description = """
        If true, plays ambient sounds while anywhere in the End.""")
    private static boolean alien = true;

    @Configurable(name = "Bleak environment sounds", requireRestart = false, description = """
        If true, plays ambient sounds in cold and/or barren overworld environments.""")
    private static boolean bleak = true;

    @Configurable(name = "Cave depth", requireRestart = false, description = """
        If true, plays more intense cave sounds when below Y 0 and light level is lower than the cave light level.""")
    private static boolean caveDepth = true;

    @Configurable(name = "Cave drone sounds", requireRestart = false, description = """
        If true, plays a low drone sound when in a cave below the cave drone cutoff.""")
    private static boolean caveDrone = true;

    @Configurable(name = "Cave drone cutoff", requireRestart = false, description = """
        Height at which the cave drone will be silenced.""")
    private static int caveDroneCutoff = 48;

    @Configurable(name = "Cave light level", requireRestart = false, description = """
        Light level at which cave ambience will be dampened.""")
    private static int caveLightLevel = 10;

    @Configurable(name = "Deepslate sounds", requireRestart = false, description = """
        If true, plays ambient sounds when the player is underground and near deepslate blocks.""")
    private static boolean deepslate = true;

    @Configurable(name = "Dry environment sounds", requireRestart = false, description = """
        If true, plays ambient sounds in dry and/or hot overworld environments.""")
    private static boolean dry = true;

    @Configurable(name = "Geode sounds", requireRestart = false, description = """
        If true, plays ambient sounds from a nearby amethyst geode.""")
    private static boolean geode = true;

    @Configurable(name = "Gravel sounds", requireRestart = false, description = """
        If true, plays ambient sounds when the player is underground and near gravel blocks.""")
    private static boolean gravel = true;

    @Configurable(name = "High sounds", requireRestart = false, description = """
        If true, plays ambient sounds when high up in the overworld.""")
    private static boolean high = true;

    @Configurable(name = "Mansion sounds", requireRestart = false, description = """
        If true, plays ambient sounds while inside a woodland mansion.""")
    private static boolean mansion = true;

    @Configurable(name = "Mineshaft sounds", requireRestart = false, description = """
    If true, plays ambient sounds from a nearby mineshaft.""")
    private static boolean mineshaft = true;

    @Configurable(name = "Night plains", requireRestart = false, description = """
        If true, plays ambient sounds in plains environments at night.""")
    private static boolean nightPlains = true;

    @Configurable(name = "Snowstorm sounds", requireRestart = false, description = """
        If true, plays ambient sounds when in a cold biome during a thunderstorm.""")
    private static boolean snowstorm = true;

    @Configurable(name = "Underground water", requireRestart = false, description = """
        If true, plays water sounds from a nearby water source when underground.""")
    private static boolean undergroundWater = true;

    @Configurable(name = "Village sounds", requireRestart = false, description = """
        If true, plays ambient sounds when a player is inside a village.""")
    private static boolean village = true;

    @Configurable(name = "Valid cave ambience dimensions", description = """
        Dimensions in which cave ambience (drone and depth) will be played.""")
    private static List<String> caveDimensions = List.of(
        "minecraft:overworld"
    );

    public Environment(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
    }

    /**
     * Helper method for classes to be able to access this feature instance.
     */
    public static Environment feature() {
        return Mod.getSidedFeature(Environment.class);
    }

    public List<String> caveDimensions() {
        return caveDimensions;
    }

    public List<ResourceLocation> validCaveDimensions() {
        if (caveDimensions().isEmpty()) return List.of();
        if (validCaveDimensions.isEmpty()) {
            caveDimensions().forEach(dim -> validCaveDimensions.add(ResourceLocation.parse(dim)));
        }
        return validCaveDimensions;
    }

    public int caveLightLevel() {
        return Mth.clamp(caveLightLevel, 0, 15);
    }

    public int caveDroneCutoff() {
        return Mth.clamp(caveDroneCutoff, -64, 1000);
    }

    public boolean alien() {
        return alien;
    }

    public boolean bleak() {
        return bleak;
    }

    public boolean caveDepth() {
        return caveDepth;
    }

    public boolean caveDrone() {
        return caveDrone;
    }

    public boolean deepslate() {
        return deepslate;
    }

    public boolean dry() {
        return dry;
    }

    public boolean geode() {
        return geode;
    }

    public boolean gravel() {
        return gravel;
    }

    public boolean high() {
        return high;
    }

    public boolean mansion() {
        return mansion;
    }

    public boolean mineshaft() {
        return mineshaft;
    }

    public boolean nightPlains() {
        return nightPlains;
    }

    public boolean snowstorm() {
        return snowstorm;
    }

    public boolean undergroundWater() {
        return undergroundWater;
    }

    public boolean village() {
        return village;
    }
}
