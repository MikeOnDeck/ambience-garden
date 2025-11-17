package charmony.villager_tasks.common.features.villager_tasks;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class Resources {
    public static final Component ABANDON = Component.translatable("gui.charmony.villager_tasks.abandon");
    public static final Component ACCEPT = Component.translatable("gui.charmony.villager_tasks.accept");
    public static final Component ACTIVE_TASKS_TITLE = Component.translatable("gui.charmony.villager_tasks.active_tasks.title");
    public static final Component ACTIVE_TASKS_BUTTON = Component.translatable("gui.charmony.villager_tasks.active_tasks.button");
    public static final Component AVAILABLE_TASKS_TITLE = Component.translatable("gui.charmony.villager_tasks.available_tasks.title");
    public static final Component AVAILABLE_TASKS_BUTTON = Component.translatable("gui.charmony.villager_tasks.available_tasks.button");
    public static final Component AVAILABLE_TASKS_TOOLTIP = Component.translatable("gui.charmony.villager_tasks.available_tasks.tooltip");
    public static final Component BACK_TO_TASKS = Component.translatable("gui.charmony.villager_tasks.back_to_tasks");
    public static final Component BATTLE_ASPECT = Component.translatable("gui.charmony.villager_tasks.aspect.battle");
    public static final Component CLOSE = Component.translatable("gui.charmony.villager_tasks.close");
    public static final Component COLLECT_ASPECT = Component.translatable("gui.charmony.villager_tasks.aspect.collect");
    public static final Component COMPLETE = Component.translatable("gui.charmony.villager_tasks.complete");
    public static final Component COMPLETE_TASKS_TITLE = Component.translatable("gui.charmony.villager_tasks.complete_tasks.title");
    public static final Component COMPLETE_TASKS_BUTTON = Component.translatable("gui.charmony.villager_tasks.complete_tasks.button");
    public static final Component COMPLETE_TASKS_TOOLTIP = Component.translatable("gui.charmony.villager_tasks.complete_tasks.tooltip");
    public static final Component CONFIRM = Component.translatable("gui.charmony.villager_tasks.confirm");
    public static final Component CONFIRM_ABANDON = Component.translatable("gui.charmony.villager_tasks.confirm_abandon");
    public static final Component DETAILS = Component.translatable("gui.charmony.villager_tasks.details");
    public static final Component DOING_TASK = Component.translatable("gui.charmony.villager_tasks.doing_task");
    public static final Component DONE_TASK = MutableComponent.create(
        Component.translatable("gui.charmony.villager_tasks.done_task").getContents()).withStyle(ChatFormatting.GREEN);
    public static final Component DONE_TASK_WITH_LOYALTY = MutableComponent.create(
        Component.translatable("gui.charmony.villager_tasks.done_task_with_loyalty").getContents()).withStyle(ChatFormatting.GREEN);
    public static final Component EFFECTS = Component.translatable("gui.charmony.villager_tasks.effects");
    public static final Component EPIC_TASK_TITLE = MutableComponent.create(
        Component.translatable("gui.charmony.villager_tasks.epic_task.title").getContents()).withStyle(ChatFormatting.GOLD);
    public static final Component EPIC_TASK_DESCRIPTION = MutableComponent.create(
        Component.translatable("gui.charmony.villager_tasks.epic_task.description").getContents()).withStyle(ChatFormatting.GRAY);
    public static final Component HUNT_ASPECT = Component.translatable("gui.charmony.villager_tasks.aspect.hunt");
    public static final Component MISSINGNO = Component.translatable("gui.charmony.villager_tasks.missingno");
    public static final Component NO_AVAILABLE_TASKS = Component.translatable("gui.charmony.villager_tasks.no_available_tasks");
    public static final Component REWARD_ASPECT = Component.translatable("gui.charmony.villager_tasks.aspect.reward");
    public static final Component REQUIREMENTS = Component.translatable("gui.charmony.villager_tasks.requirements");
    public static final Component REWARDS = Component.translatable("gui.charmony.villager_tasks.rewards");
    public static final Component TREASURE_ASPECT = Component.translatable("gui.charmony.villager_tasks.aspect.treasure");
    public static final Component TREASURE_PREFIXES = Component.translatable("gui.charmony.villager_tasks.treasure_prefixes");

    public static final MutableComponent YOU_MUST_COLLECT = Component.translatable("gui.charmony.villager_tasks.you_must_collect").withStyle(ChatFormatting.YELLOW);
    public static final MutableComponent YOU_MUST_DEFEAT = Component.translatable("gui.charmony.villager_tasks.you_must_defeat").withStyle(ChatFormatting.YELLOW);
    public static final MutableComponent YOU_MUST_HUNT = Component.translatable("gui.charmony.villager_tasks.you_must_hunt").withStyle(ChatFormatting.YELLOW);
    public static final MutableComponent YOU_MUST_DISCOVER = Component.translatable("gui.charmony.villager_tasks.you_must_discover").withStyle(ChatFormatting.YELLOW);
    public static final MutableComponent YOU_RECEIVE = Component.translatable("gui.charmony.villager_tasks.you_receive").withStyle(ChatFormatting.AQUA);

    public static final Map<Integer, Identifier> ACTIVE_SCROLL_LEVELS = new HashMap<>();
    public static final Map<Integer, Identifier> AVAILABLE_SCROLL_LEVELS = new HashMap<>();
    public static final Identifier STAR = Identifier.fromNamespaceAndPath("charmony", "task/star");
    public static final Identifier TASKS_BACKGROUND = Identifier.fromNamespaceAndPath("charmony", "textures/gui/container/tasks.png");

    static {
        for (var i = 1; i <= 6; i++) {
            ACTIVE_SCROLL_LEVELS.put(i, Identifier.fromNamespaceAndPath("charmony", "scroll/active/level" + i));
            AVAILABLE_SCROLL_LEVELS.put(i, Identifier.fromNamespaceAndPath("charmony", "scroll/available/level" + i));
        }
    }
}
