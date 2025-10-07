package svenhjol.charmony.core.common;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@SuppressWarnings("unused")
public class ConditionalSlot extends Slot {
    private final Predicate<ItemStack> condition;

    public ConditionalSlot(Predicate<ItemStack> condition, Container container, int index, int xPosition, int yPosition) {
        super(container, index, xPosition, yPosition);
        this.condition = condition;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return condition.test(stack);
    }
}
