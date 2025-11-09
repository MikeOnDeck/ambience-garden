package charmony.villager_tasks.common.features.villager_tasks.aspects;

import charmony.villager_tasks.common.features.villager_tasks.Aspect;
import charmony.villager_tasks.common.features.villager_tasks.Helpers;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.rewards.RewardEffect;
import charmony.villager_tasks.common.features.villager_tasks.rewards.RewardItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

    public Rewards copy() {
        return new Rewards(experience, new ArrayList<>(items), new ArrayList<>(effects));
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Component getName() {
        return Resources.REWARD_ASPECT;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty() && effects.isEmpty() && experience <= 0;
    }

    public List<RewardItem> items() {
        return items;
    }

    public static Rewards make(Task.AspectBuilder builder) {
        var map = builder.definition().rewards;
        if (map.isEmpty()) return EMPTY;

        var random = builder.random();
        var multiplier = builder.modifier().positiveMultiplier();

        // Resolve experience from map.
        var experience = (int) Math.round((double) map.getOrDefault("experience", 0.0d) * multiplier);

        // Resolve items from map.
        var items = (List<Map<String, Object>>)map.getOrDefault("items", List.of());
        if (items.isEmpty()) {
            throw new IllegalStateException("Aspect requires at least one item.");
        }

        var count = Math.min(items.size(), Helpers.getCountFromMap(map, multiplier, random));
        var criteria = new ArrayList<RewardItem>();

        for (var i = 0; i < items.size(); i++) {
            try {
                var itemMap = items.get(i);
                var itemId = (String) itemMap.get("item");
                var itemWeight = (double) itemMap.getOrDefault("weight", 1.0d);
                var itemStack = new ItemStack(Helpers.resolveItem(builder.registryAccess(), itemId, random));
                var itemCount = Helpers.getCountFromMap(itemMap, multiplier, random);

                if (itemStack.isEmpty()) {
                    throw new IllegalStateException("Item " + itemId + " could not be parsed");
                }

                criteria.add(new RewardItem(itemStack, itemCount, (int)itemWeight));
            } catch (Exception e) {
                log().warn(e.getMessage() + " at index " + i);
            }
        }

        if (criteria.isEmpty()) {
            return EMPTY;
        }

        var rewardItems = Helpers.getRandomlyByWeight(criteria, count, random);
        return new Rewards(experience, rewardItems, List.of());
    }

    @Override
    public void onComplete(Task task, ServerPlayer player) {
        var level = player.level();
        var stacks = new ArrayList<ItemStack>();

        if (experience > 0) {
            player.giveExperienceLevels(experience);
            level.playSound(null, player, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.25f, 1.0f);
        }

        for (var item : items()) {
            var stack = item.stack().copy();
            stack.setCount(item.total());
            stacks.add(stack);
        }

        var villager = Helpers.getNearbyTaskOwner(player, task.villager).or(() -> Helpers.getNearbyRewardGiver(player));
        villager.ifPresent(v -> Helpers.throwItemsAtPlayer(v, player, stacks));
    }
}
