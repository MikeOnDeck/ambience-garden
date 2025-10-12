package charmony.tweaks.common.features.path_converting;

import net.minecraft.server.level.ServerPlayer;
import charmony.core.base.Setup;
import charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<PathConverting> {
    public Advancements(PathConverting feature) {
        super(feature);
    }

    public void convertedPathToDirt(ServerPlayer player) {
        AdvancementHelper.trigger("converted_path_to_dirt", player);
    }
}
