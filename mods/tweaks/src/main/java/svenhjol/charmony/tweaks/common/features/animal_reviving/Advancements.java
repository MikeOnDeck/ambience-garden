package svenhjol.charmony.tweaks.common.features.animal_reviving;

import net.minecraft.server.level.ServerPlayer;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<AnimalReviving> {
    public Advancements(AnimalReviving feature) {
        super(feature);
    }

    public void revivedAnimal(ServerPlayer player) {
        AdvancementHelper.trigger("revived_animal", player);
    }
}
