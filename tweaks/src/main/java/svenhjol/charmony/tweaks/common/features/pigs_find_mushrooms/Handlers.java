package svenhjol.charmony.tweaks.common.features.pigs_find_mushrooms;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;
import svenhjol.charmony.core.base.Setup;

import java.util.*;

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
