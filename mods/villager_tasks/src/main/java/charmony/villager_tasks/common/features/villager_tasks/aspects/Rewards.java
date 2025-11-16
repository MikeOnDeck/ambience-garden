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

    public List<RewardEffect> effects() {
        return effects;
    }

    public static Rewards make(Task.AspectBuilder builder) {
        var map = builder.definition().rewards;
        if (map.isEmpty()) return EMPTY;

        var random = builder.random();
        var multiplier = builder.modifier().positiveMultiplier();
        var registryAccess = builder.registryAccess();

        // Resolve experience from map.
        var experience = (int) Math.round((double) map.getOrDefault("experience", 0.0d) * multiplier);

        // Resolve effects from map.
        var effectsMap = (List<Map<String, Object>>) map.getOrDefault("effects", List.of());
        List<RewardEffect> rewardEffects = new ArrayList<>();

        Helpers.parseStandardEffectsEntry(effectsMap, random,
            parsed -> rewardEffects.add(new RewardEffect(parsed.effect(), parsed.amplifier(), parsed.duration())));

        // Resolve items from map.
        var items = (List<Map<String, Object>>)map.getOrDefault("items", List.of());
        var itemCount = Math.min(items.size(), Helpers.getCountFromMap(map, multiplier, random));
        var parsedItems = new ArrayList<RewardItem>();

        Helpers.parseStandardItemsEntry(registryAccess, items, multiplier, random,
            parsed -> parsedItems.add(new RewardItem(parsed.stack(), parsed.count(), parsed.weight())));

        if (parsedItems.isEmpty()) return EMPTY;
        var rewardItems = Helpers.getRandomlyByWeight(parsedItems, itemCount, random);

        return new Rewards(experience, rewardItems, rewardEffects);
    }

    @Override
    public void onComplete(Task task, ServerPlayer player) {
        var level = player.level();
        var registryAccess = level.registryAccess();
        var stacks = new ArrayList<ItemStack>();

        if (experience > 0) {
            player.giveExperienceLevels(experience);
            level.playSound(null, player, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.25f, 1.0f);
        }

        for (var effect : effects()) {
            player.addEffect(effect.mobEffectInstance(registryAccess));
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
