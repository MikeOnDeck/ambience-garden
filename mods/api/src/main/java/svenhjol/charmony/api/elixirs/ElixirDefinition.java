package svenhjol.charmony.api.elixirs;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface ElixirDefinition extends StringRepresentable {
    /**
     * Unique ID for indexing and lookups.
     */
    String id();

    /**
     * Translatable name for the elixir.
     */
    MutableComponent name(RandomSource random);

    default int minDuration() {
        return 480;
    }

    default int maxDuration() {
        return 960;
    }

    default int minAmplifier() {
        return 2;
    }

    default int maxAmplifier() {
        return 5;
    }

    /**
     * Rarity to apply to the elixir. This affects the item tooltip.
     */
    default Rarity rarity() {
        return Rarity.RARE;
    }

    /**
     * Number of effects to include from the fixed or valid list.
     */
    default int numberOfEffects(RandomSource random) {
        return 1;
    }

    /**
     * Optional lore for the elixir.
     */
    default Optional<ItemLore> lore() {
        return Optional.empty();
    }

    /**
     * A list of effects to apply to the item.
     */
    default List<Holder<MobEffect>> validEffects(HolderGetter.Provider provider) {
        return List.of();
    }

    /**
     * A list of fixed effects to apply directly to the item without modification.
     */
    default List<MobEffectInstance> fixedEffects() {
        return List.of();
    }

    @Override
    default String getSerializedName() {
        return id();
    }
}
