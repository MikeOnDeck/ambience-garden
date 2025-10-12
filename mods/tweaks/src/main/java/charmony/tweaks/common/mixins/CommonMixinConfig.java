package charmony.tweaks.common.mixins;

import charmony.api.core.Side;
import charmony.core.base.MixinConfig;
import charmony.tweaks.TweaksMod;

public class CommonMixinConfig extends MixinConfig {
    @Override
    protected String modId() {
        return TweaksMod.ID;
    }

    @Override
    protected String modRoot() {
        return "charmony.tweaks";
    }

    @Override
    protected Side side() {
        return Side.Common;
    }
}
