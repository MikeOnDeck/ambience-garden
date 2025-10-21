package charmony.villager_tasks.common.features.villager_tasks.aspects;

import charmony.villager_tasks.common.features.villager_tasks.Aspect;
import charmony.villager_tasks.common.features.villager_tasks.Definition;
import charmony.villager_tasks.common.features.villager_tasks.Helpers;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.rewards.RewardEffect;
import charmony.villager_tasks.common.features.villager_tasks.rewards.RewardItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class Rewards extends Aspect {
    public static final String ID = "reward";

    public final List<RewardItem> items = new ArrayList<>();
    public final List<RewardEffect> effects = new ArrayList<>();
    public final int experience;

    public static final Codec<Rewards> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("experience").forGetter(reward -> reward.experience),
        RewardItem.CODEC.listOf().fieldOf("items").forGetter(reward -> reward.items),
        RewardEffect.CODEC.listOf().fieldOf("effects").forGetter(reward -> reward.effects)
    ).apply(instance, Rewards::new));

    public static final Rewards EMPTY = new Rewards(0, List.of(), List.of());

    public Rewards(int experience, List<RewardItem> items, List<RewardEffect> effects) {
        this.experience = experience;
        this.items.addAll(items);
        this.effects.addAll(effects);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Component getName() {
        return Resources.REWARD_ASPECT;
    }

    public static Rewards make(RegistryAccess registryAccess, Definition definition, double multiplier, RandomSource random) {
        var map = definition.rewards;
        if (map.isEmpty()) {
            return EMPTY;
        }

        // Resolve experience from map.
        var experience = (int) Math.ceil((double) map.getOrDefault("experience", 0.0d));

        // Resolve items from map.
        var items = (List<Map<String, Object>>)map.getOrDefault("items", List.of());
        if (items.isEmpty()) {
            throw new RuntimeException();
        }

        var count = Math.min(items.size(), Helpers.getCountFromMap(map, multiplier, random));
        var criteria = new ArrayList<RewardItem>();

        for (var i = 0; i < items.size(); i++) {
            try {
                var itemMap = items.get(i);
                var itemId = (String) itemMap.get("item");
                var itemWeight = (double) itemMap.getOrDefault("weight", 1.0d);
                var itemStack = new ItemStack(Helpers.resolveItem(registryAccess, itemId, random));
                var itemCount = Helpers.getCountFromMap(itemMap, multiplier, random);

                criteria.add(new RewardItem(itemStack, itemCount, (int)itemWeight));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to parse collect item at index " + i, e);
            }
        }

        var rewardItems = Helpers.getRandomlyByWeight(criteria, count, random);
        return new Rewards(experience, rewardItems, List.of());
    }
}
