package svenhjol.charmony.tweaks.client.features.jukeboxes_stop_background_music;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, description = """
    Playing a music disc in a jukebox prevents background music from playing at the same time.""")
public final class JukeboxesStopBackgroundMusic extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    public JukeboxesStopBackgroundMusic(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
    }

    public static JukeboxesStopBackgroundMusic feature() {
        return Mod.getSidedFeature(JukeboxesStopBackgroundMusic.class);
    }
}
