package svenhjol.charmony.tweaks.common.features.animal_reviving;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import svenhjol.charmony.core.base.Setup;

public class Handlers extends Setup<AnimalReviving> {
    public Handlers(AnimalReviving feature) {
        super(feature);
    }

    public void entityKilled(LivingEntity entity, DamageSource source) {
        if (entity instanceof OwnableEntity animal
            && entity.hasCustomName()
            && animal.getOwner() != null
            && !entity.level().isClientSide())
        {
            var level = entity.level();
            var pos = entity.blockPosition();

            // It's possible to dupe saddles. Set inventory slot 0 to empty.
            if (entity instanceof AbstractHorse abstractHorse) {
                var originalItem = abstractHorse.inventory.getItem(0);
                log().dev("Replacing horse inventory item " + originalItem + " with empty stack");
                abstractHorse.inventory.setItem(0, ItemStack.EMPTY);
            }

            var stack = new ItemStack(Items.NAME_TAG);
            stack.set(DataComponents.CUSTOM_NAME, entity.getDisplayName());

            try (var scopedCollector = new ProblemReporter.ScopedCollector(entity.problemPath(), feature().log().getLogger())) {
                var valueOutput = TagValueOutput.createWithContext(scopedCollector, entity.registryAccess());
                entity.save(valueOutput);
                var tag = valueOutput.buildResult();
                stack.set(feature().registers.data, Data.of(tag));
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, stack));
            }
        }
    }

    public InteractionResult useItem(Player player, Level level, InteractionHand hand) {
        InteractionHand otherHand;
        if (hand == InteractionHand.MAIN_HAND) {
            otherHand = InteractionHand.OFF_HAND;
        } else {
            otherHand = InteractionHand.MAIN_HAND;
        }

        var pos = player.blockPosition();
        var stack = player.getItemInHand(hand);
        var otherStack = player.getItemInHand(otherHand);

        if (level.isClientSide()
            || !stack.is(Items.NAME_TAG)
            || !otherStack.is(Items.TOTEM_OF_UNDYING)
            || !stack.has(feature().registers.data)
        ) {
            return InteractionResult.PASS;
        }

        var data = stack.get(feature().registers.data);
        if (data != null) {
            var tag = data.copy();
            var revived = (LivingEntity) EntityType.loadEntityRecursive(tag, level, EntitySpawnReason.SPAWN_ITEM_USE, entity -> entity);
            if (revived != null) {
                revived.setHealth(revived.getMaxHealth());
                revived.setPosRaw(pos.getX(), pos.getY(), pos.getZ());

                if (revived instanceof TamableAnimal animal) {
                    animal.setOwner(player);
                }

                level.addFreshEntity(revived);
                level.playSound(null, pos, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
                level.broadcastEntityEvent(revived, (byte)35);

                revived.move(MoverType.SELF, player.position());

                if (!player.getAbilities().instabuild) {
                    otherStack.shrink(otherStack.getCount());
                }

                stack.shrink(stack.getCount());

                feature().advancements.revivedAnimal((ServerPlayer) player);
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }
}
