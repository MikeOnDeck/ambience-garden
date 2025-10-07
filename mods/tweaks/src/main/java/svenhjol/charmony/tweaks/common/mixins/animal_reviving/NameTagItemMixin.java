package svenhjol.charmony.tweaks.common.mixins.animal_reviving;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charmony.tweaks.common.features.animal_reviving.AnimalReviving;

@Mixin(NameTagItem.class)
public abstract class NameTagItemMixin extends Item {
    public NameTagItemMixin(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(AnimalReviving.feature().registers.data);
    }
}
