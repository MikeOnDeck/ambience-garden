package charmony.tweaks.common.features.pigs_find_mushrooms;

import charmony.core.base.Setup;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class Handlers extends Setup<PigsFindMushrooms> {
    public final Map<UUID, Integer> eating = new WeakHashMap<>();

    public Handlers(PigsFindMushrooms feature) {
        super(feature);
    }

    public void entityJoin(Entity entity, Level level) {
        if (entity instanceof Pig pig) {
            var goalSelector = pig.goalSelector;
            if (goalSelector.getAvailableGoals().stream().noneMatch(
                g -> g.getGoal() instanceof FindMushroomGoal)) {
                goalSelector.addGoal(3, new FindMushroomGoal(pig));
            }
        }
    }
}
