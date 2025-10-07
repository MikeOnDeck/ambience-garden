package svenhjol.charmony.core.client.features.test_feature;

import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, canBeDisabledInConfig = false)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class TestFeature extends SidedFeature {
    public final Handlers handlers;

    @Configurable(
        name = "Purple shulker box dialogs",
        description = "If true, enable the testing gui graphics mixin"
    )
    private static boolean purpleShulkerBoxes = false;

    public TestFeature(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
    }

    public static TestFeature feature() {
        return Mod.getSidedFeature(TestFeature.class);
    }

    public boolean purpleShulkerBoxes() {
        return purpleShulkerBoxes;
    }
}
