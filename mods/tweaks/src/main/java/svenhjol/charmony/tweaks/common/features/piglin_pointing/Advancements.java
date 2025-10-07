package svenhjol.charmony.tweaks.common.features.piglin_pointing;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;
import svenhjol.charmony.core.helpers.PlayerHelper;

public class Advancements extends Setup<PiglinPointing> {
    public Advancements(PiglinPointing feature) {
        super(feature);
    }

    public void piglinProvidedDirections(ServerLevel level, BlockPos pos) {
        PlayerHelper.getPlayersInRange(level, pos, 8.0d).forEach(
            player -> AdvancementHelper.trigger("piglin_provided_directions", player));
    }
}
