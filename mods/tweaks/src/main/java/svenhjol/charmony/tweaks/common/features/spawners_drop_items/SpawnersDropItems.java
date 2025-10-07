package svenhjol.charmony.tweaks.common.features.spawners_drop_items;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = """
    Monster spawners drop mob-related items when broken. This allows items such as gunpowder, string
    and rotten flesh to be gathered in larger quantities when the game difficulty is set to peaceful.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class SpawnersDropItems extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    @Configurable(
        name = "Peaceful only",
        requireRestart = false,
        description = """
            If true, monster spawners only drop items when the game difficulty is set to peaceful.
            If false, monster spawners drop items regardless of the game difficulty.""")
    private static boolean onlyPeaceful = true;

    public SpawnersDropItems(Mod mod) {
        super(mod);

        registers = new Registers(this);
        handlers = new Handlers(this);
    }

    public static SpawnersDropItems feature() {
        return Mod.getSidedFeature(SpawnersDropItems.class);
    }

    public boolean onlyPeaceful() {
        return onlyPeaceful;
    }

    /**
     * Helper method to register new spawner drops.
     * TODO: create API interface for this.
     */
    @Deprecated
    public static void registerDropType(TagKey<EntityType<?>> entity, Item item, int amount) {
        feature().registers.registerDropType(entity, item, amount);
    }
}
