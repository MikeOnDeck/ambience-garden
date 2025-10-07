package svenhjol.charmony.api.relics;

import net.minecraft.core.HolderGetter;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
public interface RelicDefinition extends StringRepresentable {
    /**
     * Unique id for indexing and lookups.
     */
    String id();

    /**
     * Translatable name for the relic.
     */
    MutableComponent name(RandomSource random);

    /**
     * Base itemstack for the relic.
     */
    ItemStack item(RandomSource random);

    /**
     * Relic type. Can be used to sort relics into specific loot tables for example.
     */
    RelicType type();

    /**
     * Rarity to apply to the relic. This affects the item tooltip.
     */
    Rarity rarity();

    /**
     * Optional lore for the relic.
     */
    default Optional<ItemLore> lore() {
        return Optional.empty();
    }

    /**
     * Additional levels above the enchantment maximum.
     */
    default int additionalLevels(RandomSource random) {
        return 1;
    }

    /**
     * Number of enchantments to include from the fixed or valid list.
     */
    default int numberOfEnchantments(RandomSource random) {
        return 1;
    }

    default DyeColor glintColor(RandomSource random) {
        var colors = new ArrayList<>(List.of(DyeColor.values()));
        colors.remove(DyeColor.PURPLE);
        return colors.get(random.nextInt(colors.size()));
    }

    /**
     * A list of enchantments to apply to the item.
     * The max level will be calculated for each enchantment.
     */
    default List<ResourceKey<Enchantment>> validEnchantments(HolderGetter.Provider provider) {
        return List.of();
    }

    /**
     * A map of enchantments and levels to apply directly to the item without modification.
     */
    default Map<ResourceKey<Enchantment>, Integer> fixedEnchantments() {
        return Map.of();
    }

    /**
     * Enforces a maximum upper limit on all enchantments. By default this is 10 but you can override
     * this per definition if you wish. Enchantments greater than 10 will need locale strings adding,
     * as vanilla only has strings up to "enchantment.level.10".
     */
    default int upperBound(ResourceKey<Enchantment> enchantment, int level) {
        if (enchantment.equals(Enchantments.QUICK_CHARGE)) {
            return Math.min(5, level); // Quick charge is broken > 5. See https://minecraft.wiki/w/Quick_Charge#Trivia
        }
        return Math.min(10, level);
    }

    @Override
    default String getSerializedName() {
        return id();
    }
}
