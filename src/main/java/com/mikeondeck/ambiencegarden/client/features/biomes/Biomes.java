package com.mikeondeck.ambiencegarden.client.features.biomes;

import com.mikeondeck.ambiencegarden.api.core.Configurable;
import com.mikeondeck.ambiencegarden.api.core.FeatureDefinition;
import com.mikeondeck.ambiencegarden.api.core.Side;
import com.mikeondeck.ambiencegarden.core.base.Mod;
import com.mikeondeck.ambiencegarden.core.base.SidedFeature;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

@FeatureDefinition(side = Side.Client, description = """
    Plays ambient background sound according to the biome and time of day.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class Biomes extends SidedFeature {
    public static final int DEFAULT_BIOME_BLEND = 32;
    public static final List<String> DEFAULT_DIMENSIONS = List.of(
        "minecraft:overworld",
        "minecraft:the_end"
    );

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
    private static int biomeBlend = DEFAULT_BIOME_BLEND;

    @Configurable(
        name = "Valid dimensions",
        description = "Dimensions in which biome ambience will be played."
    )
    private static List<String> dimensions = new ArrayList<>(DEFAULT_DIMENSIONS);

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

    public void setBiomeBlend(int biomeBlend) {
        Biomes.biomeBlend = biomeBlend;
    }

    public void setDimensionEnabled(String id, boolean enabled) {
        var next = new ArrayList<>(dimensions());
        if (enabled) {
            if (!next.contains(id)) next.add(id);
        } else {
            next.remove(id);
        }
        dimensions = next;
        validDimensions.clear();
    }

    public void resetToDefaults() {
        biomeBlend = DEFAULT_BIOME_BLEND;
        dimensions = new ArrayList<>(DEFAULT_DIMENSIONS);
        validDimensions.clear();
        enabled(true);
    }

    public List<Identifier> validDimensions() {
        if (dimensions().isEmpty()) return List.of();
        if (validDimensions.isEmpty()) {
            dimensions().forEach(dim -> validDimensions.add(Identifier.parse(dim)));
        }
        return validDimensions;
    }
}
