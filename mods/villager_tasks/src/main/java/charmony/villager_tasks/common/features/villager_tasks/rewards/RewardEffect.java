package charmony.villager_tasks.common.features.villager_tasks.rewards;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record RewardEffect(ResourceLocation effectId, float duration, float amplifier) {
    public static final Codec<RewardEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("effectId").forGetter(self -> self.effectId),
        Codec.FLOAT.fieldOf("duration").forGetter(self -> self.duration),
        Codec.FLOAT.fieldOf("amplifier").forGetter(self -> self.amplifier)
    ).apply(instance, RewardEffect::new));
}
