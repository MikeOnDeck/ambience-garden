package charmony.tweaks.common.features.mob_drops.mobs;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import charmony.core.base.Setup;
import charmony.core.helpers.EnchantmentsHelper;
import charmony.tweaks.common.features.mob_drops.DropProvider;
import charmony.tweaks.common.features.mob_drops.MobDrops;

import java.util.Optional;

public class GoatDrops extends Setup<MobDrops> implements DropProvider {
    public GoatDrops(MobDrops feature) {
        super(feature);
    }

    @Override
    public Optional<ItemStack> dropWhenKilled(LivingEntity entity, DamageSource source) {
        if (entity instanceof Goat goat) {
            var chance = feature().maxMuttonDrops() + 1 + (EnchantmentsHelper.lootingLevel(source));
            var amount = goat.getRandom().nextInt(chance);
            var item = goat.isOnFire() ? Items.COOKED_MUTTON : Items.MUTTON;
            if (amount > 0) return Optional.of(new ItemStack(item, amount));
        }
        return Optional.empty();
    }
}
