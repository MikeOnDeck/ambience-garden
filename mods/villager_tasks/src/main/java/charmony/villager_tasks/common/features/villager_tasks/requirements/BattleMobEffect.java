package charmony.villager_tasks.common.features.villager_tasks.requirements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public record BattleMobEffect(ResourceLocation effect, int amplifier, int duration) {
    public static final Codec<BattleMobEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("effect").forGetter(self -> self.effect),
        Codec.INT.fieldOf("amplifier").forGetter(self -> self.amplifier),
        Codec.INT.fieldOf("duration").forGetter(self -> self.duration)
    ).apply(instance, BattleMobEffect::new));

    public MobEffectInstance mobEffectInstance(Registry<MobEffect> registry) {
        var effect = registry.get(effect()).orElseThrow();
        return new MobEffectInstance(effect, duration, amplifier);
    }
}
