package svenhjol.charmony.tweaks.client.mixins;

import svenhjol.charmony.core.base.MixinConfig;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.tweaks.TweaksMod;

public class ClientMixinConfig extends MixinConfig {
    @Override
    protected String modId() {
        return TweaksMod.ID;
    }

    @Override
    protected String modRoot() {
        return "svenhjol.charmony.tweaks";
    }

    @Override
    protected Side side() {
        return Side.Client;
    }
}
