package charmony.villager_tasks.common.features.villager_tasks.interfaces;

import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.loot.LootTable;

public interface EventListener {
    default void onStart(ServerPlayer player) {
        // hook
    }

    default void onStarted(ServerPlayer player) {
        // hook
    }

    default void onTick(ServerPlayer player) {
        // hook
    }

    default void onAbandon(ServerPlayer player) {
        // hook
    }

    default void onComplete(ServerPlayer player) {
        // hook
    }

    default void onEntityKilled(LivingEntity livingEntity, DamageSource damageSource) {
        // hook
    }

    default void onEntityLeave(Entity entity) {
        // hook
    }

    default void onItemPickup(LivingEntity livingEntity, ItemEntity itemEntity) {
        // hook
    }

    default void onLootTableModify(ResourceKey<LootTable> key, LootTable.Builder builder, LootTableSource source, HolderLookup.Provider provider) {
        // hook
    }
}
