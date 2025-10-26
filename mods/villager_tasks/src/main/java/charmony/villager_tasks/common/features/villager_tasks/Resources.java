package charmony.villager_tasks.common.features.villager_tasks;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class Resources {
    public static final Component ABANDON = Component.translatable("gui.charmony.villager_tasks.abandon");
    public static final Component ACCEPT = Component.translatable("gui.charmony.villager_tasks.accept");
    public static final Component ACTIVE_TASKS_TITLE = Component.translatable("gui.charmony.villager_tasks.active_tasks.title");
    public static final Component ACTIVE_TASKS_BUTTON = Component.translatable("gui.charmony.villager_tasks.active_tasks.button");
    public static final Component AVAILABLE_TASKS_TITLE = Component.translatable("gui.charmony.villager_tasks.available_tasks.title");
    public static final Component AVAILABLE_TASKS_BUTTON = Component.translatable("gui.charmony.villager_tasks.available_tasks.button");
    public static final Component COLLECT_ASPECT = Component.translatable("gui.charmony.villager_tasks.aspect.collect");
    public static final Component COMPLETE = Component.translatable("gui.charmony.villager_tasks.complete");
    public static final Component CONFIRM = Component.translatable("gui.charmony.villager_tasks.confirm");
    public static final Component CONFIRM_ABANDON = Component.translatable("gui.charmony.villager_tasks.confirm_abandon");
    public static final Component DETAILS = Component.translatable("gui.charmony.villager_tasks.details");
    public static final Component DOING_TASK = Component.translatable("gui.charmony.villager_tasks.doing_task");
    public static final Component MISSINGNO = Component.translatable("gui.charmony.villager_tasks.missingno");
    public static final Component NO_ACTIVE_TASKS = Component.translatable("gui.charmony.villager_tasks.no_active_tasks");
    public static final Component NO_AVAILABLE_TASKS = Component.translatable("gui.charmony.villager_tasks.no_available_tasks");
    public static final Component REQUIRES_LABEL = Component.translatable("gui.charmony.villager_tasks.label.requires");
    public static final Component REWARD_ASPECT = Component.translatable("gui.charmony.villager_tasks.aspect.reward");
    public static final Component REWARDS_LABEL = Component.translatable("gui.charmony.villager_tasks.label.rewards");
    public static final Component REQUIREMENTS = Component.translatable("gui.charmony.villager_tasks.requirements");
    public static final Component REWARDS = Component.translatable("gui.charmony.villager_tasks.rewards");
    public static final Component TASKS = Component.translatable("gui.charmony.villager_tasks.tasks");

    public static final Component YOU_COLLECT = MutableComponent.create(
        Component.translatable("gui.charmony.villager_tasks.you_collect").getContents()).withStyle(ChatFormatting.RED, ChatFormatting.BOLD);

    public static final Component YOU_COLLECTED = MutableComponent.create(
        Component.translatable("gui.charmony.villager_tasks.you_collected").getContents()).withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD);

    public static final Component YOU_RECEIVE = MutableComponent.create(
        Component.translatable("gui.charmony.villager_tasks.you_receive").getContents()).withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);


    public static final Map<Integer, ResourceLocation> ACTIVE_LEVELS = new HashMap<>();
    public static final Map<Integer, ResourceLocation> AVAILABLE_LEVELS = new HashMap<>();

    public static final ResourceLocation TASKS_BACKGROUND = ResourceLocation.fromNamespaceAndPath("charmony", "textures/gui/container/tasks.png");

    static {
        for (var i = 1; i <= 6; i++) {
            ACTIVE_LEVELS.put(i, ResourceLocation.fromNamespaceAndPath("charmony", "active/level" + i));
            AVAILABLE_LEVELS.put(i, ResourceLocation.fromNamespaceAndPath("charmony", "available/level" + i));
        }
    }
}
