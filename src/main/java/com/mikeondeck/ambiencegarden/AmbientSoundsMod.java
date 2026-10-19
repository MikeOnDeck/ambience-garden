package com.mikeondeck.ambiencegarden;

import com.mikeondeck.ambiencegarden.api.core.ModDefinition;
import com.mikeondeck.ambiencegarden.core.base.Mod;
import com.mikeondeck.ambiencegarden.api.core.Side;

@ModDefinition(id = AmbientSoundsMod.ID, sides = {Side.Client},
    name = "Ambient sounds",
    description = "Simple ambient sounds for Minecraft. Compatible with vanilla servers such as Realms.")
public class AmbientSoundsMod extends Mod {
    public static final String ID = "ambience_garden";

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
