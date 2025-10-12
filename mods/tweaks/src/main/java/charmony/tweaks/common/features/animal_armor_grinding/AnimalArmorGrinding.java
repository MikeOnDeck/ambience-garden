package charmony.tweaks.common.features.animal_armor_grinding;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

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
