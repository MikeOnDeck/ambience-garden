package svenhjol.charmony.tweaks.common.features.animal_armor_grinding;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = """
    Animal armor returns a single ingot, leather, scute or diamond when used on the grindstone.""")
public final class AnimalArmorGrinding extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;
    public final Advancements advancements;
    public final GrindableItemProviders grindableItemProviders;

    public AnimalArmorGrinding(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
        advancements = new Advancements(this);
        grindableItemProviders = new GrindableItemProviders(this);
    }
}
