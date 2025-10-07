package svenhjol.charmony.tweaks.common.features.totems_work_from_inventory;

import svenhjol.charmony.api.tweaks.TotemInventoryCheckProvider;
import svenhjol.charmony.core.Api;
import svenhjol.charmony.core.base.Setup;

import java.util.ArrayList;
import java.util.List;

public class Registers extends Setup<TotemsWorkFromInventory> {
    public final List<TotemInventoryCheckProvider> inventoryCheckProviders = new ArrayList<>();

    public Registers(TotemsWorkFromInventory feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            Api.consume(TotemInventoryCheckProvider.class, inventoryCheckProviders::add);
        };
    }
}
