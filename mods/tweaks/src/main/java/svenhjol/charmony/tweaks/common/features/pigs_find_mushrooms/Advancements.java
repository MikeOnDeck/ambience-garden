package svenhjol.charmony.tweaks.common.features.pigs_find_mushrooms;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;
import svenhjol.charmony.core.helpers.PlayerHelper;

public class Advancements extends Setup<PigsFindMushrooms> {
    public Advancements(PigsFindMushrooms feature) {
        super(feature);
    }

    public void unearthedMushroom(Level level, BlockPos pos) {
        PlayerHelper.getPlayersInRange(level, pos, 8.0d).forEach(
            player -> AdvancementHelper.trigger("unearthed_mushroom", player));
    }
}
