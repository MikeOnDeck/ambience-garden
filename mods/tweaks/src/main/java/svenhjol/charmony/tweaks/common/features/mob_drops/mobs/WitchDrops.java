package svenhjol.charmony.tweaks.common.features.mob_drops.mobs;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.EnchantmentsHelper;
import svenhjol.charmony.tweaks.common.features.mob_drops.DropProvider;
import svenhjol.charmony.tweaks.common.features.mob_drops.MobDrops;

import java.util.Optional;

public class WitchDrops extends Setup<MobDrops> implements DropProvider {
    public static final double LOOTING_MULTIPLIER = 0.1d;

    public WitchDrops(MobDrops feature) {
        super(feature);
    }

    @Override
    public Optional<ItemStack> dropWhenKilled(LivingEntity entity, DamageSource source) {
        if (entity instanceof Witch witch) {
            var chance = feature().potionOfLuckDropChance() + (EnchantmentsHelper.lootingLevel(source) * LOOTING_MULTIPLIER);
            if (witch.getRandom().nextDouble() <= chance) {
                if (source.getEntity() instanceof ServerPlayer player) {
                    feature().advancements.droppedPotionOfLuck(player);
                }
                return Optional.of(PotionContents.createItemStack(Items.POTION, Potions.LUCK));
            }
        }
        return Optional.empty();
    }
}
