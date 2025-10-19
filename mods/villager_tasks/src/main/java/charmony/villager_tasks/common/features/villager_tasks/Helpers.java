package charmony.villager_tasks.common.features.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.interfaces.HasWeight;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
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
            var imin = (int)(min * multiplier);
            var imax = (int)(max * multiplier);
            count = random.nextIntBetweenInclusive(imin, imax);
        }

        return count;
    }

    public static <T extends HasWeight> List<T> getItemsRandomlyByWeight(List<T> items, int count, RandomSource random) {
        var selected = new ArrayList<T>();

        items.sort((a, b) -> Integer.compare(b.getWeight(), a.getWeight()));
        var totalWeight = items.stream().mapToInt(T::getWeight).sum();

        for (var i = 0; i < count; i++) {
            var r = random.nextInt(totalWeight);
            var cumulative = 0;

            for (var item : items) {
                cumulative += item.getWeight();
                if (r < cumulative) {
                    selected.add(item);
                    totalWeight -= item.getWeight();
                    items.remove(item);
                    break;
                }
            }
        }

        return selected;
    }
}
