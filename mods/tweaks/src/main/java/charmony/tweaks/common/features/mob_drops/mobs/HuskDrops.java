package charmony.tweaks.common.features.mob_drops.mobs;

import charmony.core.base.Setup;
import charmony.core.helpers.EnchantmentsHelper;
import charmony.tweaks.common.features.mob_drops.DropProvider;
import charmony.tweaks.common.features.mob_drops.MobDrops;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;

public class HuskDrops extends Setup<MobDrops> implements DropProvider {
    public HuskDrops(MobDrops feature) {
        super(feature);
    }

    @Override
    public Optional<ItemStack> dropWhenKilled(LivingEntity entity, DamageSource source) {
        if (entity instanceof Husk husk) {
            var chance = feature().maxSandDrops() + 1 + (EnchantmentsHelper.lootingLevel(source));
            var amount = husk.getRandom().nextInt(chance);
            if (amount > 0) {
                if (source.getEntity() instanceof ServerPlayer player) {
                    feature().advancements.droppedSand(player);
                }
                return Optional.of(new ItemStack(Items.SAND, amount));
            }
        }
        return Optional.empty();
    }
}
