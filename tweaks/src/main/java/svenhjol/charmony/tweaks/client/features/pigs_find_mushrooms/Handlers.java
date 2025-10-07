package svenhjol.charmony.tweaks.client.features.pigs_find_mushrooms;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Pig;
import svenhjol.charmony.core.base.Environment;
import svenhjol.charmony.core.base.Setup;

public class Handlers extends Setup<PigsFindMushrooms> {
    // Reference to the currently rendering mob.
    public Pig pig;

    public Handlers(PigsFindMushrooms feature) {
        super(feature);
    }

    public float getHeadEatPositionScale(Pig pig, float f) {
        if (!Environment.usesCharmonyServer()) return f;

        var tick = common().handlers.eating.getOrDefault(pig.getUUID(), 0);
        if (tick <= 0) {
            return 0;
        }
        if (tick >= 4 && tick <= 36) {
            return 1;
        }
        if (tick < 4) {
            return ((float)tick - f) / 4.0f;
        }
        return -((float)(tick - 40) - f) / 4.0f;
    }

    public float getHeadEatAngleScale(Pig pig, float f) {
        if (!Environment.usesCharmonyServer()) return f;

        var tick = common().handlers.eating.getOrDefault(pig.getUUID(), 0);
        if (tick > 4 && tick <= 36) {
            float g = ((float)(tick - 4) - f) / 32.0f;
            return 0.63f + 0.22f * Mth.sin(g * 28.7f);
        }
        if (tick > 0) {
            return 0.63f;
        }
        return pig.getXRot() * ((float)Math.PI / 180);
    }

    private Common common() {
        return PigsFindMushrooms.feature().common.get();
    }
}
