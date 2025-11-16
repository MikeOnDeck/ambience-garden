package charmony.villager_tasks.common.features.villager_tasks.data;

import charmony.villager_tasks.common.features.villager_tasks.interfaces.HasWeight;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public record RewardItem(ItemStack stack, int total, int weight) implements HasWeight {
    public static final Codec<RewardItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemStack.CODEC.fieldOf("stack").forGetter(self -> self.stack),
        Codec.INT.fieldOf("total").forGetter(self -> self.total),
        Codec.INT.fieldOf("weight").forGetter(self -> self.weight)
    ).apply(instance, RewardItem::new));
}
