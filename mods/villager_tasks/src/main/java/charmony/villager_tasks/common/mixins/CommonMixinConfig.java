package charmony.villager_tasks.common.mixins;

import charmony.api.core.Side;
import charmony.core.base.MixinConfig;
import charmony.villager_tasks.VillagerTasksMod;

public class CommonMixinConfig extends MixinConfig {
    @Override
    protected String modId() {
        return VillagerTasksMod.ID;
    }

    @Override
    protected String modRoot() {
        return "charmony.villager_tasks";
    }

    @Override
    protected Side side() {
        return Side.Common;
    }
}
