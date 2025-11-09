package charmony.villager_tasks.common.mixins.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.VillagerTasks;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BrushableBlockEntity.class)
public class BrushableBlockEntityMixin {
    @ModifyExpressionValue(
        method = "unpackLootTable",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;J)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"
        )
    )
    private ObjectArrayList<ItemStack> hookUnpackLootTable(ObjectArrayList<ItemStack> original) {
        return VillagerTasks.feature().handlers.brushableBlockLootCheck(original).orElse(original);
    }
}
