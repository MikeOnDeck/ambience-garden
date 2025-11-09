package charmony.villager_tasks.common.mixins.villager_tasks;

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
    private ObjectArrayList<ItemStack> hookUnpackLootTable(ObjectArrayList<ItemStack> list) {
        // If additional pool added to an archaeology loot table, always use the additional pool.
        if (list.size() == 2) {
            return ObjectArrayList.of(list.get(1));
        }

        return list;
    }
}
