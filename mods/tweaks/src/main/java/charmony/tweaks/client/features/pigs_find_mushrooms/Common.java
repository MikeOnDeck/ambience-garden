package charmony.tweaks.client.features.pigs_find_mushrooms;

import charmony.tweaks.common.features.pigs_find_mushrooms.Handlers;
import charmony.tweaks.common.features.pigs_find_mushrooms.PigsFindMushrooms;

public class Common {
    public final Handlers handlers;

    public Common() {
        var feature = PigsFindMushrooms.feature();
        handlers = feature.handlers;
    }
}
