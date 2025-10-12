package charmony.ambient_sounds.client.features.environment;

import charmony.ambient_sounds.client.features.environment.sounds.*;
import charmony.ambient_sounds.client.features.sound.SoundType;
import charmony.core.base.Setup;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Registers extends Setup<Environment> {
    public final SoundType<EnvironmentSound> alien = new Alien();
    public final SoundType<EnvironmentSound> bleak = new Bleak();
    public final SoundType<EnvironmentSound> caveDrone = new CaveDrone();
    public final SoundType<EnvironmentSound> caveDepth = new CaveDepth();
    public final SoundType<EnvironmentSound> deepslate = new Deepslate();
    public final SoundType<EnvironmentSound> dry = new Dry();
    public final SoundType<EnvironmentSound> geode = new Geode();
    public final SoundType<EnvironmentSound> gravel = new Gravel();
    public final SoundType<EnvironmentSound> high = new High();
    public final SoundType<EnvironmentSound> mansion = new Mansion();
    public final SoundType<EnvironmentSound> mineshaft = new Mineshaft();
    public final SoundType<EnvironmentSound> nightPlains = new NightPlains();
    public final SoundType<EnvironmentSound> snowstorm = new Snowstorm();
    public final SoundType<EnvironmentSound> undergroundWater = new UndergroundWater();
    public final SoundType<EnvironmentSound> village = new Village();

    public Registers(Environment feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            ClientEntityEvents.ENTITY_LOAD.register(feature().handlers::clientEntityJoin);
            ClientEntityEvents.ENTITY_UNLOAD.register(feature().handlers::clientEntityLeave);
            ClientTickEvents.END_CLIENT_TICK.register(feature().handlers::clientTick);
        };
    }
}
