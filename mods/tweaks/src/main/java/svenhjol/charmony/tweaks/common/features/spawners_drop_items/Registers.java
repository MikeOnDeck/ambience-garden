package svenhjol.charmony.tweaks.common.features.spawners_drop_items;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import svenhjol.charmony.core.base.Setup;

public class Registers extends Setup<SpawnersDropItems> {
    public final Table<TagKey<EntityType<?>>, Item, Integer> dropTypes = HashBasedTable.create();

    public Registers(SpawnersDropItems feature) {
        super(feature);

        registerDropType(Tags.SPAWNER_DROPS_BLAZE_RODS, Items.BLAZE_ROD, 32);
        registerDropType(Tags.SPAWNER_DROPS_BONES, Items.BONE, 64);
        registerDropType(Tags.SPAWNER_DROPS_GUNPOWDER, Items.GUNPOWDER, 128);
        registerDropType(Tags.SPAWNER_DROPS_MAGMA_CREAM, Items.MAGMA_CREAM, 64);
        registerDropType(Tags.SPAWNER_DROPS_ROTTEN_FLESH, Items.ROTTEN_FLESH, 64);
        registerDropType(Tags.SPAWNER_DROPS_SLIME_BALLS, Items.SLIME_BALL, 128);
        registerDropType(Tags.SPAWNER_DROPS_SPIDER_EYES, Items.SPIDER_EYE, 64);
        registerDropType(Tags.SPAWNER_DROPS_STRING, Items.STRING, 64);
    }

    @Override
    public Runnable boot() {
        return () -> {
            PlayerBlockBreakEvents.AFTER.register(feature().handlers::blockBreak);
        };
    }

    public void registerDropType(TagKey<EntityType<?>> entity, Item item, int amount) {
        dropTypes.put(entity, item, amount);
    }
}
