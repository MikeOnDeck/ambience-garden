package svenhjol.charmony.tweaks.common.features.animal_reviving;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import svenhjol.charmony.core.base.Setup;

public class Registers extends Setup<AnimalReviving> {
    public final DataComponentType<Data> data;

    public Registers(AnimalReviving feature) {
        super(feature);

        data = DataComponents.register("revived_animal", builder -> builder
            .persistent(Data.CODEC)
            .networkSynchronized(Data.STREAM_CODEC));
    }

    @Override
    public Runnable boot() {
        return () -> {
            UseItemCallback.EVENT.register(feature().handlers::useItem);
            ServerLivingEntityEvents.AFTER_DEATH.register(feature().handlers::entityKilled);
        };
    }
}
