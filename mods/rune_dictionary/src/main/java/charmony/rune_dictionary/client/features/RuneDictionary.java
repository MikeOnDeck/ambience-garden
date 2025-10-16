package charmony.rune_dictionary.client.features;

import charmony.api.core.FeatureDefinition;
import charmony.api.core.Side;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;

import java.util.function.Supplier;

@FeatureDefinition(side = Side.Client, canBeDisabledInConfig = false)
@SuppressWarnings("unused")
public final class RuneDictionary extends SidedFeature {
    public final Supplier<Common> common;
    public final Registers registers;
    public final Handlers handlers;

    public RuneDictionary(Mod mod) {
        super(mod);
        common = Common::new;
        handlers = new Handlers(this);
        registers = new Registers(this);
    }

    public static RuneDictionary feature() {
        return Mod.getSidedFeature(RuneDictionary.class);
    }
}
