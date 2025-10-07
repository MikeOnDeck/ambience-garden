package svenhjol.charmony.core.common.dispenser;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import svenhjol.charmony.core.common.CommonRegistry;

import java.util.List;

public class CompositeDispenseItemBehavior extends DefaultDispenseItemBehavior {
    private final DispenseItemBehavior originalBehavior;

    public CompositeDispenseItemBehavior(DispenseItemBehavior originalBehavior) {
        this.originalBehavior = originalBehavior;
    }

    @Override
    public ItemStack execute(BlockSource blockSource, ItemStack stack) {
        var behaviors = CommonRegistry.CONDITIONAL_DISPENSER_BEHAVIORS
            .getOrDefault(stack.getItem(), List.of());

        for (var behavior : behaviors) {
            var result = behavior.accept(this, blockSource, stack);
            if (result) {
                return behavior.stack().orElse(stack);
            }
        }

        if (originalBehavior instanceof DefaultDispenseItemBehavior defaultBehavior) {
            return defaultBehavior.execute(blockSource, stack);
        } else {
            return super.execute(blockSource, stack);
        }
    }
}
