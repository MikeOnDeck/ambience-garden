package charmony.tweaks.common.features.grindstone_disenchanting;

import charmony.core.base.Environment;
import charmony.core.base.Setup;
import charmony.api.events.GrindstoneEvents;

import java.util.function.BooleanSupplier;

public class Registers extends Setup<GrindstoneDisenchanting> {
    public Registers(GrindstoneDisenchanting feature) {
        super(feature);
    }

    @Override
    public BooleanSupplier check() {
        return () -> !Environment.isModLoaded("grindenchantments");
    }

    @Override
    public Runnable boot() {
        return () -> {
            GrindstoneEvents.ON_TAKE.handle(feature().handlers::onTakeFromGrindstone);
            GrindstoneEvents.CALCULATE_OUTPUT.handle(feature().handlers::calculateGrindstoneOutput);
            GrindstoneEvents.CAN_TAKE.handle(feature().handlers::canTakeFromGrindstone);
            GrindstoneEvents.CAN_PLACE.handle(feature().handlers::canPlaceOnGrindstone);
        };
    }
}
