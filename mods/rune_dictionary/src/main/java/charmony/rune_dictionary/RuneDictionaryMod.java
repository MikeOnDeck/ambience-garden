package charmony.rune_dictionary;

import charmony.api.core.ModDefinition;
import charmony.api.core.Side;
import charmony.core.base.Mod;
import net.minecraft.resources.Identifier;

@ModDefinition(
    id = RuneDictionaryMod.ID,
    sides = {Side.Client, Side.Common},
    name = "Rune Dictionary",
    description = "Library mod to add rune words and player knowledge.")
public final class RuneDictionaryMod extends Mod {
    public static final String ID = "charmony-rune-dictionary";
    private static RuneDictionaryMod instance;

    private RuneDictionaryMod() {}

    public static RuneDictionaryMod instance() {
        if (instance == null) {
            instance = new RuneDictionaryMod();
        }
        return instance;
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(ID, path);
    }
}