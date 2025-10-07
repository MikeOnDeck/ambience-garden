package svenhjol.charmony.core.client.mixins;

import svenhjol.charmony.core.Charmony;
import svenhjol.charmony.core.base.MixinConfig;
import svenhjol.charmony.api.core.Side;

public final class ClientMixinConfig extends MixinConfig {
    @Override
    protected String modId() {
        return Charmony.ID;
    }

    @Override
    protected String modRoot() {
        return "svenhjol.charmony.core";
    }

    @Override
    protected Side side() {
        return Side.Client;
    }
}
