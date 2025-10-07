package svenhjol.charmony.tweaks.common.features.animal_damage_immunity;

import net.minecraft.server.level.ServerPlayer;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<AnimalDamageImmunity> {
    public Advancements(AnimalDamageImmunity feature) {
        super(feature);
    }
    
    public void preventedAnimalDamage(ServerPlayer player) {
        AdvancementHelper.trigger("prevented_animal_damage", player);
    }
}
