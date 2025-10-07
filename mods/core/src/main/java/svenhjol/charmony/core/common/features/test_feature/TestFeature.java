package svenhjol.charmony.core.common.features.test_feature;

import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.common.CommonRegistry;
import svenhjol.charmony.core.common.features.conditional_recipes.ConditionalRecipe;

import java.util.Map;

@FeatureDefinition(side = Side.Common)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "unused"})
public final class TestFeature extends SidedFeature {
    public final Registers registers;

    @Configurable(
        name = "Allow creeper spawn egg recipe",
        description = "If true, add a test recipe to craft a creeper spawn egg.",
        requireRestart = false
    )
    private static boolean allowCreeperSpawnEggRecipe = true;

    public TestFeature(Mod mod) {
        super(mod);
        registers = new Registers(this);
    }

    @Override
    public void run() {
        var registry = CommonRegistry.forFeature(this);

        registry.conditionalRecipe(new ConditionalRecipe(id("creeper_spawn_egg"), recipe -> allowCreeperSpawnEggRecipe)
            .useShapedCrafting()
            .withPattern(" O ", "OXO", " O ")
            .withKey(Map.of("O", "minecraft:egg", "X", "minecraft:gunpowder"))
            .withCount(1)
            .withResult("minecraft:creeper_spawn_egg"));
    }
}
