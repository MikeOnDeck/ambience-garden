package charmony.villager_tasks.common.features.villager_tasks.requirements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BattleMobStats(int health) {
    public static final Codec<BattleMobStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("health").forGetter(self -> self.health)
    ).apply(instance, BattleMobStats::new));
}
