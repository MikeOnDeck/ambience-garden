package svenhjol.charmony.tweaks.common.mixins.pigs_find_mushrooms;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import svenhjol.charmony.tweaks.common.features.pigs_find_mushrooms.PigsFindMushrooms;

@Mixin(Pig.class)
public abstract class PigMixin extends Animal {
    protected PigMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == 10) {
            feature().handlers.eating.put(getUUID(), 40);
        } else {
            super.handleEntityEvent(b);
        }
    }

    @SuppressWarnings("Java8MapApi")
    @Override
    public void aiStep() {
        var handlers = feature().handlers;
        var uuid = getUUID();
        if (handlers.eating.containsKey(uuid)) {
            var ticks = handlers.eating.get(uuid);
            handlers.eating.put(uuid, ticks - 1);
        }
        super.aiStep();
    }

    @Unique
    private PigsFindMushrooms feature() {
        return PigsFindMushrooms.feature();
    }
}
