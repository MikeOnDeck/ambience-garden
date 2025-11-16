package charmony.villager_tasks.common.features.villager_tasks.aspects;

import charmony.villager_tasks.common.features.villager_tasks.Aspect;
import charmony.villager_tasks.common.features.villager_tasks.Helpers;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.data.HuntMob;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Hunt extends Aspect implements Satisfiable {
    public static final String ID = "hunt";

    private final List<HuntMob> mobs;

    public static final Codec<Hunt> CODEC = HuntMob.CODEC.listOf().fieldOf("mobs")
        .xmap(Hunt::new, hunt -> hunt.mobs).codec();

    public static final Hunt EMPTY = new Hunt(List.of());

    public Hunt(List<HuntMob> mobs) {
        this.mobs = mobs;
    }

    @SuppressWarnings("unchecked")
    public static Hunt make(Task.AspectBuilder builder) {
        var map = builder.definition().hunt;
        if (map.isEmpty()) return EMPTY;

        var random = builder.random();
        var multiplier = builder.modifier().negativeMultiplier();

        // Resolve mobs from map.
        var mobs = (List<Map<String, Object>>)map.getOrDefault("mobs", List.of());
        if (mobs.isEmpty()) {
            throw new IllegalStateException("Aspect requires at least one mob.");
        }

        var count = Math.min(mobs.size(), Helpers.getCountFromMap(map, multiplier, random));
        var criteria = new ArrayList<HuntMob>();

        for (var i = 0; i < mobs.size(); i++) {
            try {
                var mobMap = mobs.get(i);
                var mobStr = (String) mobMap.get("entity");
                var mobId = Identifier.tryParse(mobStr);
                if (mobId == null) {
                    throw new IllegalStateException("Invalid mob ID " + mobStr);
                }
                var mobWeight = (double) mobMap.getOrDefault("weight", 1.0d);
                var mobCount = Helpers.getCountFromMap(mobMap, multiplier, random);

                criteria.add(new HuntMob(mobId, mobCount, 0, (int)mobWeight));
            } catch (Exception e) {
                log().warn(e.getMessage() + " at index " + i);
            }
        }

        if (criteria.isEmpty()) {
            return EMPTY;
        }

        var huntMobs = Helpers.getRandomlyByWeight(criteria, count, random);
        return new Hunt(huntMobs);
    }

    @Override
    public Hunt copy() {
        return new Hunt(mobs.stream().map(HuntMob::copy).toList());
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Component getName() {
        return Resources.HUNT_ASPECT;
    }

    @Override
    public boolean isEmpty() {
        return mobs().isEmpty();
    }

    @Override
    public boolean isSatisfied() {
        return remaining() == 0;
    }

    @Override
    public int remaining() {
        return mobs().stream().mapToInt(HuntMob::remaining).sum();
    }

    @Override
    public int total() {
        return mobs().stream().mapToInt(HuntMob::total).sum();
    }

    @Override
    public boolean onEntityKilled(Task task, LivingEntity entity, DamageSource source) {
        if (!(entity.level() instanceof ServerLevel level)) {
            return false;
        }

        // Must be the player who killed the entity.
        if (!(source.getEntity() instanceof ServerPlayer)) {
            return false;
        }

        var entityRegistry = level.registryAccess().lookup(Registries.ENTITY_TYPE).orElse(null);
        if (entityRegistry == null) return false;

        for (var req : mobs()) {
            if (req.onEntityKilled(entityRegistry, entity)) {
                return true;
            }
        }

        return false;
    }

    public List<HuntMob> mobs() {
        return mobs;
    }
}
