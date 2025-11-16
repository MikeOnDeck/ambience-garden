package charmony.villager_tasks.common.features.villager_tasks.rewards;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

public record RewardEffect(Identifier effect, int amplifier, int duration) {
    public static final Codec<RewardEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Identifier.CODEC.fieldOf("effect").forGetter(self -> self.effect),
        Codec.INT.fieldOf("amplifier").forGetter(self -> self.amplifier),
        Codec.INT.fieldOf("duration").forGetter(self -> self.duration)
    ).apply(instance, RewardEffect::new));

    public MobEffectInstance mobEffectInstance(RegistryAccess registryAccess) {
        var registry = registryAccess.lookupOrThrow(Registries.MOB_EFFECT);
        var key = ResourceKey.create(Registries.MOB_EFFECT, effect);
        var effect = registry.get(key).orElseThrow();
        return new MobEffectInstance(effect, duration, amplifier);
    }

    public ItemStack makePotion(RegistryAccess registryAccess) {
        var effect = mobEffectInstance(registryAccess);
        var contents = new PotionContents(Potions.WATER).withEffectAdded(effect);
        var potion = new ItemStack(Items.SPLASH_POTION);
        potion.set(DataComponents.POTION_CONTENTS, contents);
        return potion;
    }

    public Component name(RegistryAccess registryAccess) {
        var registry = registryAccess.lookupOrThrow(Registries.MOB_EFFECT);
        var effect = registry.get(this.effect).orElseThrow();
        return effect.value().getDisplayName();
    }
}
