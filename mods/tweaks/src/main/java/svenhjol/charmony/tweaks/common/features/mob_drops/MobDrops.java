package svenhjol.charmony.tweaks.common.features.mob_drops;

import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = "Some mobs have a chance to drop additional items either by chance or when killed.")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class MobDrops extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;
    public final Advancements advancements;

    @Configurable(name = "Feather drop chance", description = "1 in X chance of a chicken dropping a feather, per game tick.", requireRestart = false)
    private static int featherDropChance = 7500;

    @Configurable(name = "Maximum cobweb drops", description = "Maximum cobwebs dropped when a cave spider is killed.", requireRestart = false)
    private static int maxCobwebDrops = 2;

    @Configurable(name = "Maximum mutton drops", description = "Maximum pieces of mutton dropped when a goat is killed.", requireRestart = false)
    private static int maxMuttonDrops = 2;

    @Configurable(name = "Maximum sand drops", description = "Maximum sand blocks dropped when a husk is killed.", requireRestart = false)
    private static int maxSandDrops = 2;

    @Configurable(name = "Potion of Luck drop chance", description = "Chance (out of 1.0) of a witch dropping a Potion of Luck when killed.", requireRestart = false)
    private static double potionOfLuckDropChance = 0.05d;

    public MobDrops(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
        advancements = new Advancements(this);
    }

    public int featherDropChance() {
        return Mth.clamp(featherDropChance, 100, 20000);
    }

    public int maxCobwebDrops() {
        return Mth.clamp(maxCobwebDrops, 0, Item.DEFAULT_MAX_STACK_SIZE);
    }

    public int maxMuttonDrops() {
        return Mth.clamp(maxMuttonDrops, 0, Item.DEFAULT_MAX_STACK_SIZE);
    }

    public int maxSandDrops() {
        return Mth.clamp(maxSandDrops, 0, Item.DEFAULT_MAX_STACK_SIZE);
    }

    public double potionOfLuckDropChance() {
        return Mth.clamp(potionOfLuckDropChance, 0.0d, 1.0d);
    }
}
