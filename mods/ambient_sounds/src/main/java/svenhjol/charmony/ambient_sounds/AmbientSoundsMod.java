package svenhjol.charmony.ambient_sounds;

import svenhjol.charmony.api.core.ModDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.api.core.Side;

@ModDefinition(id = AmbientSoundsMod.ID, sides = {Side.Client},
    name = "Ambient sounds",
    description = "Simple ambient sounds for Minecraft. Compatible with vanilla servers such as Realms.")
public class AmbientSoundsMod extends Mod {
    public static final String ID = "charmony-ambient-sounds";

    private static AmbientSoundsMod instance;

    public static AmbientSoundsMod instance() {
        if (instance == null) {
            instance = new AmbientSoundsMod();
        }
        return instance;
    }

    @Override
    public String id() {
        return ID;
    }
}
