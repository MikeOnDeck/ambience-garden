package svenhjol.charmony.tweaks.common.features.path_converting;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.sounds.SoundEvent;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.CommonRegistry;

import java.util.function.Supplier;

public class Registers extends Setup<PathConverting> {
    public final Supplier<SoundEvent> dirtToPathSound;
    public final Supplier<SoundEvent> pathToDirtSound;

    public Registers(PathConverting feature) {
        super(feature);
        var registry = CommonRegistry.forFeature(feature);

        dirtToPathSound = registry.sound("convert_dirt_to_path");
        pathToDirtSound = registry.sound("convert_path_to_dirt");
    }

    @Override
    public Runnable boot() {
        return () -> {
            UseBlockCallback.EVENT.register(feature().handlers::useBlock);
        };
    }
}
