package svenhjol.charmony.tweaks.common.features.mob_drops.mobs;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.tweaks.common.features.mob_drops.DropProvider;
import svenhjol.charmony.tweaks.common.features.mob_drops.MobDrops;

import java.util.Optional;

public class ChickenDrops extends Setup<MobDrops> implements DropProvider {
    public ChickenDrops(MobDrops feature) {
        super(feature);
    }

    @Override
    public Optional<ItemStack> dropByChance(LivingEntity entity) {
        if (entity instanceof Chicken chicken) {
            if (chicken.isAlive()
                && !chicken.level().isClientSide()
                && !chicken.isBaby()
                && !chicken.isChickenJockey()
                && chicken.level().random.nextInt(feature().featherDropChance()) == 0
            ) {
                var random = chicken.level().random;
                chicken.playSound(feature().registers.shedFeatherSound.get(), 0.2f, 0.9f + (random.nextFloat() * 0.5f));
                return Optional.of(new ItemStack(Items.FEATHER));
            }
        }

        return Optional.empty();
    }
}
