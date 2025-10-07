package svenhjol.charmony.ambient_sounds.client.features.biomes;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import svenhjol.charmony.ambient_sounds.client.features.biomes.sounds.*;
import svenhjol.charmony.ambient_sounds.client.features.sound.SoundType;
import svenhjol.charmony.core.base.Setup;

public class Registers extends Setup<Biomes> {
    public final SoundType<BiomeSound> badlands = new Badlands();
    public final SoundType<BiomeSound> beach = new Beach();
    public final SoundType<BiomeSound> caves = new Caves();
    public final SoundType<BiomeSound> desert = new Desert();
    public final SoundType<BiomeSound> forest = new Forest();
    public final SoundType<BiomeSound> icy = new Icy();
    public final SoundType<BiomeSound> jungle = new Jungle();
    public final SoundType<BiomeSound> mountains = new Mountains();
    public final SoundType<BiomeSound> ocean = new Ocean();
    public final SoundType<BiomeSound> paleGarden = new PaleGarden();
    public final SoundType<BiomeSound> plains = new Plains();
    public final SoundType<BiomeSound> river = new River();
    public final SoundType<BiomeSound> savanna = new Savanna();
    public final SoundType<BiomeSound> swamp = new Swamp();
    public final SoundType<BiomeSound> taiga = new Taiga();
    public final SoundType<BiomeSound> theEnd = new TheEnd();

    public Registers(Biomes feature) {
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
