package charmony.tweaks.common.features.respawn_anchors_work_everywhere;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, enabledByDefault = false, description = """
    The repsawn anchor can be used in any dimension.
    This feature changes core gameplay so is disabled by default.""")
public final class RespawnAnchorsWorkEverywhere extends SidedFeature {
    public RespawnAnchorsWorkEverywhere(Mod mod) {
        super(mod);
    }
}
