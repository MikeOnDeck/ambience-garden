package svenhjol.charmony.tweaks.client.features.pigs_find_mushrooms;

import svenhjol.charmony.tweaks.common.features.pigs_find_mushrooms.Handlers;
import svenhjol.charmony.tweaks.common.features.pigs_find_mushrooms.PigsFindMushrooms;

public class Common {
    public final Handlers handlers;

    public Common() {
        var feature = PigsFindMushrooms.feature();
        handlers = feature.handlers;
    }
}
