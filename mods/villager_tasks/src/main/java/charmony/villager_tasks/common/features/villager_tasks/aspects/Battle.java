package charmony.villager_tasks.common.features.villager_tasks.aspects;

import charmony.core.helpers.UuidHelper;
import charmony.villager_tasks.common.features.villager_tasks.Aspect;
import charmony.villager_tasks.common.features.villager_tasks.Helpers;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.data.*;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Util;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Battle extends Aspect implements Satisfiable {
    public static final String ID = "battle";

    private final List<BattleMob> mobs;
    private final List<BattleAtmosphere> atmosphere;
    private final BattleMobSpawn spawn;
    private boolean defeated;

    public static final Codec<Battle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BattleMob.CODEC.listOf().fieldOf("mobs").forGetter(self -> self.mobs),
        BattleAtmosphere.CODEC.listOf().fieldOf("atmosphere").forGetter(self -> self.atmosphere),
        BattleMobSpawn.CODEC.fieldOf("spawn").forGetter(self -> self.spawn),
        Codec.BOOL.fieldOf("defeated").forGetter(self -> self.defeated)
    ).apply(instance, Battle::new));

    public static final Battle EMPTY = new Battle(List.of(), List.of(), BattleMobSpawn.EMPTY, false);

    public Battle(List<BattleMob> mobs, List<BattleAtmosphere> atmosphere, BattleMobSpawn spawn, boolean defeated) {
        this.defeated = defeated;
        this.mobs = mobs;
        this.spawn = spawn;
        this.atmosphere = atmosphere;
    }

    @SuppressWarnings("unchecked")
    public static Battle make(Task.AspectBuilder builder) {
        var map = builder.definition().battle;
        if (map.isEmpty()) return EMPTY;

        var level = builder.player().level();
        var registryAccess = level.registryAccess();
        var dimension = level.dimension().identifier();
        var random = builder.random();
        var multiplier = builder.modifier().negativeMultiplier();

        var mobs = (List<Map<String, Object>>) map.getOrDefault("mobs", List.of());
        if (mobs.isEmpty()) {
            throw new IllegalStateException("Aspect requires at least one mob.");
        }

        var count = Math.min(mobs.size(), Helpers.getCountFromMap(map, multiplier, random));
        var criteria = new ArrayList<BattleMob>();

        // Decide where to place the blockPos for spawning.
        var distance = (double) map.getOrDefault("distance", 0d);
        var structure = (String) map.getOrDefault("structure", "");
        var biome = (String) map.getOrDefault("biome", "");
        var spawn = BattleMobSpawn.make(registryAccess, distance, structure, biome, random);

        // Get atmosphere entries.
        var atmosphereList = (List<Object>) map.getOrDefault("atmosphere", List.of());
        List<BattleAtmosphere> atmosphere = new ArrayList<>();

        for (var i = 0; i < atmosphereList.size(); i++) {
            try {
                var str = (String) atmosphereList.get(i);
                BattleAtmosphere.fromString(str).ifPresent(atmosphere::add);
            } catch (Exception e) {
                log().warn(e.getMessage() + " at index " + i);
            }
        }

        for (var i = 0; i < mobs.size(); i++) {
            try {
                // Parse mob entry.
                var mobMap = mobs.get(i);
                var mobStr = (String) mobMap.get("entity");
                var mobId = Identifier.tryParse(mobStr);
                if (mobId == null) {
                    throw new IllegalStateException("Invalid mob ID " + mobStr);
                }
                var mobCount = Helpers.getCountFromMap(mobMap, multiplier, random);
                var uniqueId = UuidHelper.fromRandom(random);

                // Parse effects to apply to these mobs.
                var effectsMap = (List<Map<String, Object>>) mobMap.getOrDefault("effects", List.of());
                List<Effect> effects = new ArrayList<>();

                Helpers.parseStandardEffectsEntry(effectsMap, random,
                    parsed -> effects.add(new Effect(parsed.effect(), parsed.amplifier(), MobEffectInstance.INFINITE_DURATION)));

                // Parse equipment to add to these mobs.
                var equipmentMap = (List<Map<String, Object>>) mobMap.getOrDefault("equipment", Map.of());
                List<Equipment> equipment = new ArrayList<>();

                Helpers.parseStandardEquipmentEntry(registryAccess, equipmentMap, random,
                    parsed -> equipment.add(new Equipment(parsed.slot(), parsed.stack())));

                var stats = new BattleMobData(mobId, effects, equipment);
                criteria.add(new BattleMob(stats, uniqueId, dimension,  Optional.empty(), mobCount, 0, false));
            } catch (Exception e) {
                log().warn(e.getMessage() + " at index " + i);
            }
        }

        if (criteria.isEmpty()) {
            return EMPTY;
        }

        Util.shuffle(criteria, random);
        var list = criteria.subList(0, Math.min(count, criteria.size()));
        return new Battle(list, atmosphere, spawn, false);
    }

    @Override
    public Battle copy() {
        return new Battle(mobs.stream().map(BattleMob::copy).toList(), new ArrayList<>(atmosphere), spawn.copy(), defeated);
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

        if (!defeated && player.level() instanceof ServerLevel level && isSatisfied()) {
            defeated = true;
            clearAtmosphere(level);
        }
    }

    @Override
    public void onComplete(Task task, ServerPlayer player) {
        super.onComplete(task, player);

        if (player.level() instanceof ServerLevel level) {
            clearAtmosphere(level);
        }
    }

    @Override
    public void onAbandon(Task task, ServerPlayer player) {
        super.onAbandon(task, player);

        if (player.level() instanceof ServerLevel level) {
            clearAtmosphere(level);
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

    public BattleMobSpawn spawn() {
        return spawn;
    }

    public List<BattleAtmosphere> atmosphere() {
        return atmosphere;
    }

    public List<BattleMob> mobs() {
        return mobs;
    }

    private void clearAtmosphere(ServerLevel level) {
        if (atmosphere().contains(BattleAtmosphere.Storm)) {
            level.setWeatherParameters(12000, 24000, false, false);
        }
    }
}
