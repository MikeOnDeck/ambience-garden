package svenhjol.charmony.tweaks.common.features.suspicious_block_creating;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;
import svenhjol.charmony.core.helpers.PlayerHelper;

public class Advancements extends Setup<SuspiciousBlockCreating> {
    public Advancements(SuspiciousBlockCreating feature) {
        super(feature);
    }

    public void createdSuspiciousBlock(ServerLevel level, BlockPos pos) {
        PlayerHelper.getPlayersInRange(level, pos, 8.0d).forEach(
            player -> AdvancementHelper.trigger("created_suspicious_block", player));
    }
}
