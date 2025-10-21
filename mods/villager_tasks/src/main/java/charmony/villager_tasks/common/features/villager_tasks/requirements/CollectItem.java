package charmony.villager_tasks.common.features.villager_tasks.requirements;

import charmony.villager_tasks.common.features.villager_tasks.interfaces.HasWeight;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public record CollectItem(ItemStack stack, int total, int weight) implements HasWeight {
    public static final Codec<CollectItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemStack.CODEC.fieldOf("stack").forGetter(self -> self.stack),
        Codec.INT.fieldOf("total").forGetter(self -> self.total),
        Codec.INT.fieldOf("weight").forGetter(self -> self.weight)
    ).apply(instance, CollectItem::new));

}
