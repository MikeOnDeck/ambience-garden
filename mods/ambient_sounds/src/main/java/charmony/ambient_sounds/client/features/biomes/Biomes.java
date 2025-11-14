package charmony.ambient_sounds.client.features.biomes;

import charmony.api.core.Configurable;
import charmony.api.core.FeatureDefinition;
import charmony.api.core.Side;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@FeatureDefinition(side = Side.Client, description = """
    Plays ambient background sound according to the biome and time of day.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class Biomes extends SidedFeature {
    private final List<Identifier> validDimensions = new ArrayList<>();

    public final Registers registers;
    public final Handlers handlers;

    @Configurable(
        name = "Biome sound blending",
        description = """
            Number of blocks to check for neighbouring biomes.
            Set to zero to disable.""",
        requireRestart = false
    )
    private static int biomeBlend = 32;

    @Configurable(
        name = "Valid dimensions",
        description = "Dimensions in which biome ambience will be played."
    )
    private static List<String> dimensions = Arrays.asList(
        "minecraft:overworld",
        "minecraft:the_end"
    );

    public Biomes(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
    }

    public static Biomes feature() {
        return Mod.getSidedFeature(Biomes.class);
    }

    public int biomeBlend() {
        return Mth.clamp(biomeBlend, 0, 256);
    }

    public List<String> dimensions() {
        return dimensions;
    }

    public List<Identifier> validDimensions() {
        if (dimensions().isEmpty()) return List.of();
        if (validDimensions.isEmpty()) {
            dimensions().forEach(dim -> validDimensions.add(Identifier.parse(dim)));
        }
        return validDimensions;
    }
}
