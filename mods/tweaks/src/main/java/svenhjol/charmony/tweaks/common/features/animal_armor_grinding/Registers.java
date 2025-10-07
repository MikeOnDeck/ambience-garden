package svenhjol.charmony.tweaks.common.features.animal_armor_grinding;

import net.minecraft.world.level.ItemLike;
import svenhjol.charmony.api.events.GrindstoneEvents;
import svenhjol.charmony.api.tweaks.GrindableItemProvider;
import svenhjol.charmony.core.Api;
import svenhjol.charmony.core.base.Setup;

import java.util.HashMap;
import java.util.Map;

public  class Registers extends Setup<AnimalArmorGrinding> {
    public final Map<ItemLike, ItemLike> recipes = new HashMap<>();

    public Registers(AnimalArmorGrinding feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            Api.consume(GrindableItemProvider.class,
                provider -> provider.getItemGrindResults().forEach(
                    result -> feature().registers.recipes.put(result.getFirst(), result.getSecond())));

            GrindstoneEvents.CAN_PLACE.handle(feature().handlers::handleCanPlace);
            GrindstoneEvents.CALCULATE_OUTPUT.handle(feature().handlers::handleCalculateOutput);
            GrindstoneEvents.ON_TAKE.handle(feature().handlers::handleOnTake);
        };
    }
}
