package charmony.core;

import charmony.api.core.ModDefinition;
import charmony.api.core.Side;
import charmony.core.base.Mod;
import net.minecraft.resources.Identifier;

@SuppressWarnings("unused")
@ModDefinition(id = Charmony.ID, sides = {Side.Client, Side.Common},
    name = "Charmony core",
    description = "Core library for Charmony mods.")
public final class Charmony extends Mod {
    public static final String ID = "charmony";

    private static Charmony instance;

    public static Charmony instance() {
        if (instance == null) {
            instance = new Charmony();
        }
        return instance;
    }

    public static Identifier id(String path) {
        return Identifier.tryBuild(ID, path);
    }
}
