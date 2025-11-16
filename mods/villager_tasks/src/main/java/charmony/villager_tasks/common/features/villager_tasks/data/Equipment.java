package charmony.villager_tasks.common.features.villager_tasks.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public record Equipment(EquipmentSlot slot, ItemStack stack) {
    public static final Codec<Equipment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        EquipmentSlot.CODEC.fieldOf("slot").forGetter(self -> self.slot),
        ItemStack.CODEC.fieldOf("stack").forGetter(self -> self.stack)
    ).apply(instance, Equipment::new));
}
