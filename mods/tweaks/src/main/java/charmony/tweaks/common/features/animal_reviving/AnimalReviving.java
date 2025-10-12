package charmony.tweaks.common.features.animal_reviving;

import charmony.api.core.Configurable;
import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = """
    A tamed animal with a name drops its name tag on death.
    Right-click (use) the name tag while holding a Totem of Undying to revive the animal and consume the totem.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class AnimalReviving extends SidedFeature {
    public final Advancements advancements;
    public final Handlers handlers;
    public final Registers registers;

    @Configurable(
        name = "Drop loot on death",
        description = """
            If true, tamed and named animals drop items from their loot table (such as leather from horses) when they die.
            This could be unwanted behavior as it can be used to dupe items by killing and resurrecting an animal repeatedly."""
    )
    private static boolean dropLootOnDeath = false;

    @Configurable(
        name = "Drop experience on death",
        description = """
            If true, tamed and named animals drop experience when they die. This could be unwanted behavior as it can be used
            to receive an increasing amount of experience by killing and resurrecting an animal repeatedly."""
    )
    private static boolean dropExperienceOnDeath = false;

    public AnimalReviving(Mod mod) {
        super(mod);
        registers = new Registers(this);
        advancements = new Advancements(this);
        handlers = new Handlers(this);
    }

    public static AnimalReviving feature() {
        return Mod.getSidedFeature(AnimalReviving.class);
    }

    public boolean dropLootOnDeath() {
        return dropLootOnDeath;
    }

    public boolean dropExperienceOnDeath() {
        return dropExperienceOnDeath;
    }
}
