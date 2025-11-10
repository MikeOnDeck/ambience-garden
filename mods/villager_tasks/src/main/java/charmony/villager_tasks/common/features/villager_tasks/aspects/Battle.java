package charmony.villager_tasks.common.features.villager_tasks.aspects;

import charmony.core.helpers.UuidHelper;
import charmony.villager_tasks.common.features.villager_tasks.Aspect;
import charmony.villager_tasks.common.features.villager_tasks.Helpers;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import charmony.villager_tasks.common.features.villager_tasks.requirements.BattleMob;
import charmony.villager_tasks.common.features.villager_tasks.requirements.BattleMobEffect;
import charmony.villager_tasks.common.features.villager_tasks.requirements.BattleMobStats;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Battle extends Aspect implements Satisfiable {
    public static final String ID = "battle";

    private final List<BattleMob> mobs;

    public static final Codec<Battle> CODEC = BattleMob.CODEC.listOf().fieldOf("mobs")
        .xmap(Battle::new, battle -> battle.mobs).codec();

    public static final Battle EMPTY = new Battle(List.of());

    public Battle(List<BattleMob> mobs) {
        this.mobs = mobs;
    }

    @SuppressWarnings("unchecked")
    public static Battle make(Task.AspectBuilder builder) {
        var map = builder.definition().battle;
        if (map.isEmpty()) return EMPTY;

        var level = builder.player().level();
        var dimension = level.dimension().location();
        var random = builder.random();
        var multiplier = builder.modifier().negativeMultiplier();

        var mobs = (List<Map<String, Object>>) map.getOrDefault("mobs", List.of());
        if (mobs.isEmpty()) {
            throw new IllegalStateException("Aspect requires at least one mob.");
        }

        var count = Math.min(mobs.size(), Helpers.getCountFromMap(map, multiplier, random));
        var criteria = new ArrayList<BattleMob>();

        for (var i = 0; i < mobs.size(); i++) {
            try {
                // Parse mob entry.
                var mobMap = mobs.get(i);
                var mobStr = (String) mobMap.get("entity");
                var mobId = ResourceLocation.tryParse(mobStr);
                if (mobId == null) {
                    throw new IllegalStateException("Invalid mob ID " + mobStr);
                }
                var mobCount = Helpers.getCountFromMap(mobMap, multiplier, random);

                var spawnDistance = (double) mobMap.getOrDefault("distance", 128.0d);
                var uniqueId = UuidHelper.fromRandom(random);

                var health = (double) mobMap.getOrDefault("health", 20.0d);
                var stats = new BattleMobStats((int)health);

                // Parse effects to apply to these mobs.
                var effects = (List<Map<String, Object>>) mobMap.getOrDefault("effects", List.of());
                List<BattleMobEffect> effectList = new ArrayList<>();

                for (var j = 0; j < effects.size(); j++) {
                    var effectMap = effects.get(j);
                    var effectStr = (String) effectMap.get("effect");
                    var amplifier = (double) effectMap.getOrDefault("amplifier", 0.0d);
                    var duration = (double) effectMap.getOrDefault("duration", 288000.0d);
                    var effectId = ResourceLocation.tryParse(effectStr);
                    if (effectId == null) {
                        throw new IllegalStateException("Invalid effect ID " + effectStr);
                    }

                    effectList.add(new BattleMobEffect(effectId, (int)amplifier, (int)duration));
                }

                criteria.add(new BattleMob(mobId, dimension,  Optional.empty(), effectList,
                    stats, uniqueId, (int)spawnDistance, mobCount, 0, false));
            } catch (Exception e) {
                log().warn(e.getMessage() + " at index " + i);
            }
        }

        if (criteria.isEmpty()) {
            return EMPTY;
        }

        Util.shuffle(criteria, random);
        var list = criteria.subList(0, Math.min(count, criteria.size()));
        return new Battle(list);
    }

    @Override
    public Battle copy() {
        return new Battle(mobs.stream().map(BattleMob::copy).toList());
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Component getName() {
        return Resources.BATTLE_ASPECT;
    }

    @Override
    public boolean isEmpty() {
        return mobs.isEmpty();
    }

    @Override
    public boolean isSatisfied() {
        return remaining() == 0;
    }

    @Override
    public int remaining() {
        return mobs.stream().mapToInt(BattleMob::remaining).sum();
    }

    @Override
    public int total() {
        return mobs().stream().mapToInt(BattleMob::total).sum();
    }

    @Override
    public void onStart(Task task, ServerPlayer player) {
        mobs().forEach(mob -> mob.onStart(task, player));
    }

    @Override
    public void onTick(Task task, Player player) {
        super.onTick(task, player);

        if (player instanceof ServerPlayer serverPlayer) {
            var registryAccess = serverPlayer.level().registryAccess();
            mobs().forEach(mob -> mob.onTick(task, registryAccess, serverPlayer));
        }
    }

    @Override
    public boolean onEntityKilled(Task task, LivingEntity entity, DamageSource source) {
        if (!(entity.level() instanceof ServerLevel level)) {
            return false;
        }

        for (var req : mobs()) {
            if (req.onEntityKilled(level.registryAccess(), entity)) {
                return true;
            }
        }

        return false;
    }

    public List<BattleMob> mobs() {
        return mobs;
    }
}
