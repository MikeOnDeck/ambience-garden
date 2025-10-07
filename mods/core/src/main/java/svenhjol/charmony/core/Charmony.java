package svenhjol.charmony.core;

import net.minecraft.resources.ResourceLocation;
import svenhjol.charmony.api.core.ModDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.api.core.Side;

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

    public static ResourceLocation id(String path) {
        return ResourceLocation.tryBuild(ID, path);
    }
}
