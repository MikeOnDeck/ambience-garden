package charmony.core.client.mixins;

import charmony.api.core.Side;
import charmony.core.Charmony;
import charmony.core.base.MixinConfig;

public final class ClientMixinConfig extends MixinConfig {
    @Override
    protected String modId() {
        return Charmony.ID;
    }

    @Override
    protected String modRoot() {
        return "charmony.core";
    }

    @Override
    protected Side side() {
        return Side.Client;
    }
}
