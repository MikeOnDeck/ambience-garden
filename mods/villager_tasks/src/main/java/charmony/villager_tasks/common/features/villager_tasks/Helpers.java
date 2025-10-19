package charmony.villager_tasks.common.features.villager_tasks;

import net.minecraft.util.RandomSource;

import java.util.Map;

public final class Helpers {
    /**
     * Get a count from a map with min, max, exactly values and apply a multiplier.
     * This is a commonly used pattern in villager task definitions.
     */
    public static int getCountFromMap(Map<String, Object> map, double multiplier, RandomSource random) {
        int count;

        var min = (double)map.getOrDefault("min", 1.0d);
        var max = (double)map.getOrDefault("max", 1.0d);
        var exactly = map.getOrDefault("exactly", null);

        if (exactly != null) {
            count = (int)((double)exactly * multiplier);
        } else {
            var mmin = (int)(min * multiplier);
            var mmax = (int)(max * multiplier);
            count = random.nextIntBetweenInclusive(mmin, mmax);
        }

        return count;
    }
}
