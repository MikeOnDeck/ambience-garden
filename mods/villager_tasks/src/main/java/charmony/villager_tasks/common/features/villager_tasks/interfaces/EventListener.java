package charmony.villager_tasks.common.features.villager_tasks.interfaces;

import charmony.villager_tasks.common.features.villager_tasks.Task;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface EventListener {
    default void onStart(Task task, ServerPlayer player) {
        // hook
    }

    default void onStarted(Task task, ServerPlayer player) {
        // hook
    }

    default void onTick(Task task, Player player) {
        // hook
    }

    default void onAbandon(Task task, ServerPlayer player) {
        // hook
    }

    default void onComplete(Task task, ServerPlayer player) {
        // hook
    }

    default boolean onEntityKilled(Task task, LivingEntity livingEntity, DamageSource damageSource) {
        return false; // False to show that it was not handled and should pass to other handlers.
    }

    default void onEntityLeave(Task task, Entity entity) {
        // hook
    }

    default void onItemPickup(Task task, Player player, ItemStack itemStack) {
        // hook
    }

    default Optional<ItemStack> onLootTablePopulate(Task task, Player player, Identifier lootTableId, RandomSource random) {
        return Optional.empty();
    }
}
