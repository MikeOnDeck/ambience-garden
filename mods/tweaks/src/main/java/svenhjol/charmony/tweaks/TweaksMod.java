package svenhjol.charmony.tweaks;

import svenhjol.charmony.api.core.ModDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;

@ModDefinition(
    id = TweaksMod.ID,
    sides = {Side.Client, Side.Common},
    name = "Tweaks",
    description = "Small tweaks that don't change core gameplay. Compatible with vanilla servers such as Realms.")
public final class TweaksMod extends Mod {
    public static final String ID = "charmony-tweaks";

    private static TweaksMod instance;

    private TweaksMod() {}

    public static TweaksMod instance() {
        if (instance == null) {
            instance = new TweaksMod();
        }
        return instance;
    }
}
