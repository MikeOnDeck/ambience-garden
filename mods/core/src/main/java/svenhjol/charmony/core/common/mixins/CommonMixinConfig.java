package svenhjol.charmony.core.common.mixins;

import svenhjol.charmony.core.Charmony;
import svenhjol.charmony.core.base.MixinConfig;
import svenhjol.charmony.api.core.Side;

public final class CommonMixinConfig extends MixinConfig {
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
        return Side.Common;
    }
}
