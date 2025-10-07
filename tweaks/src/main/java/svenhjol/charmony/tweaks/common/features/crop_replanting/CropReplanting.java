package svenhjol.charmony.tweaks.common.features.crop_replanting;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = """
    Right-click with a hoe to quickly harvest and replant a fully-grown crop.""")
public final class CropReplanting extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;
    public final QuickReplantProviders quickReplantProviders;
    public final Advancements advancements;

    public CropReplanting(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
        quickReplantProviders = new QuickReplantProviders(this);
        advancements = new Advancements(this);
    }
}
