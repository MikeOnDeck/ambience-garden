package charmony.villager_tasks.common.features.villager_tasks.data;

import charmony.villager_tasks.common.features.villager_tasks.Tasks;
import charmony.villager_tasks.common.features.villager_tasks.VillagerTasks;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class TreasureLootFunction extends LootItemConditionalFunction {
    private final LootContext.EntityTarget entityTarget;
    private final Identifier lootTableId;

    public static final MapCodec<TreasureLootFunction> CODEC = RecordCodecBuilder.mapCodec(
        instance -> TreasureLootFunction.commonFields(instance)
            .and(LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter(func -> func.entityTarget))
            .and(Identifier.CODEC.fieldOf("lootTableId").forGetter(func -> func.lootTableId))
            .apply(instance, TreasureLootFunction::new));

    public TreasureLootFunction(List<LootItemCondition> conditions, LootContext.EntityTarget entityTarget, Identifier lootTableId) {
        super(conditions);
        this.entityTarget = entityTarget;
        this.lootTableId = lootTableId;
    }

    public TreasureLootFunction(ResourceKey<LootTable> id) {
        this(List.of(), LootContext.EntityTarget.THIS, id.identifier());
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return null;
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        Player player = null;
        var param = context.getOptionalParameter(this.entityTarget.contextParam());

        if (param instanceof Player p) {
            player = p;
        } else if (param instanceof FishingHook f) {
            player = f.getPlayerOwner();
        }

        if (player == null) {
            return stack;
        }

        var tasks = VillagerTasks.feature().handlers.getActiveTasks(player).orElse(Tasks.EMPTY).tasks();
        for (var task : tasks) {
            var result = task.onLootTablePopulate(task, player, lootTableId, context.getRandom());
            if (result.isPresent()) {
                return result.get();
            }
        }

        return stack;
    }
}
