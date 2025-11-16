package charmony.villager_tasks.common.features.villager_tasks.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;

public record BattleMobData(Identifier mob, List<Effect> effects) {
    public static final Codec<BattleMobData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Identifier.CODEC.fieldOf("mob").forGetter(self -> self.mob),
        Effect.CODEC.listOf().fieldOf("effects").forGetter(self -> self.effects)
    ).apply(instance, BattleMobData::new));

    public BattleMobData copy() {
        return new BattleMobData(mob, List.copyOf(effects));
    }
}
