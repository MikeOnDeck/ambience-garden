package charmony.villager_tasks.common.features.villager_tasks.data;

import charmony.core.base.Log;
import charmony.core.helpers.TagHelper;
import charmony.core.helpers.WorldHelper;
import charmony.villager_tasks.common.features.villager_tasks.VillagerTasks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;

public record BattleMobSpawn(BattleMobLocation type, String identifier, double distance) {
    public static final Codec<BattleMobSpawn> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BattleMobLocation.CODEC.fieldOf("type").forGetter(self -> self.type),
        Codec.STRING.fieldOf("identifier").forGetter(self -> self.identifier),
        Codec.DOUBLE.fieldOf("distance").forGetter(self -> self.distance)
    ).apply(instance, BattleMobSpawn::new));

    public static BattleMobSpawn EMPTY = new BattleMobSpawn(BattleMobLocation.Unspecified, "", 0.0);

    public static BattleMobSpawn make(RegistryAccess registryAccess, double distance, String structure, String biome, RandomSource random) {
        BattleMobLocation locationType;
        String identifier;

        if (!structure.isEmpty()) {
            var registry = registryAccess.lookupOrThrow(Registries.STRUCTURE);
            var values = TagHelper.parseAndGetValues(registry, structure);
            if (values.isEmpty()) {
                throw new IllegalStateException("No structures resolved for structure: " + structure);
            }

            Util.shuffle(values, random);
            var key = values.getFirst().unwrapKey().orElseThrow();
            identifier = key.identifier().toString();
            locationType = BattleMobLocation.Structure;
            log().debug("BattleMobSpawn using structure: " + identifier);
        } else if (!biome.isEmpty()) {
            var registry = registryAccess.lookupOrThrow(Registries.BIOME);
            var values = TagHelper.parseAndGetValues(registry, biome);
            if (values.isEmpty()) {
                throw new IllegalStateException("No biomes resolved for biome: " + biome);
            }

            Util.shuffle(values, random);
            var key = values.getFirst().unwrapKey().orElseThrow();
            identifier = key.identifier().toString();
            locationType = BattleMobLocation.Biome;
            log().debug("BattleMobSpawn using biome: " + identifier);
        } else {
            locationType = BattleMobLocation.Unspecified;
            identifier = "";
            log().debug("BattleMobSpawn using location with distance: " + distance);
        }

        return new BattleMobSpawn(locationType, identifier, distance);
    }

    public BlockPos getSpawnPosition(ServerLevel level, BlockPos startPos, RandomSource random) {
        var registryAccess = level.registryAccess();

        if (distance > 0) {
            var d = distance / 2;
            startPos =  WorldHelper.addRandomOffset(level, startPos, random, (int)d / 2, (int)d);
        }

        var locatePos = switch (type) {
            case Structure -> {
                var registry = registryAccess.lookupOrThrow(Registries.STRUCTURE);
                var structure = registry.get(Identifier.parse(identifier)).map(HolderSet::direct).orElseThrow();
                var located = level.getChunkSource().getGenerator().findNearestMapStructure(
                    level,
                    structure,
                    startPos,
                    100,
                    false
                );
                if (located == null) {
                    log().warn("Could not find structure: " + identifier + ", defaulting to start position.");
                    yield startPos;
                } else {
                    yield located.getFirst();
                }
            }
            case Biome -> {
                var biomeId = Identifier.parse(identifier);
                var located = level.findClosestBiome3d(biomeHolder -> biomeHolder.is(biomeId), startPos, 6400, 32, 64);
                if (located == null) {
                    log().warn("Could not find biome: " + identifier + ", defaulting to start position.");
                    yield startPos;
                } else {
                    yield located.getFirst();
                }
            }
            default -> startPos;
        };

        return new BlockPos(locatePos.getX(), 0, locatePos.getZ());
    }

    public BattleMobSpawn copy() {
        return new BattleMobSpawn(type, identifier, distance);
    }

    private static Log log() {
        return VillagerTasks.feature().log();
    }
}
