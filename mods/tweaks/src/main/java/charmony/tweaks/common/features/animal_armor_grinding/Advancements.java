package charmony.tweaks.common.features.animal_armor_grinding;

import net.minecraft.server.level.ServerPlayer;
import charmony.core.base.Setup;
import charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<AnimalArmorGrinding> {
    public Advancements(AnimalArmorGrinding feature) {
        super(feature);
    }

    public void groundAnimalArmor(ServerPlayer player) {
        AdvancementHelper.trigger("ground_animal_armor", player);
    }
}
