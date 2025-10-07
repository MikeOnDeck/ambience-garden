package svenhjol.charmony.ambient_sounds.client.features.environment;

import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.ambient_sounds.helpers.SettingsHelper;
import svenhjol.charmony.core.helpers.WorldHelper;

public abstract class SurfaceEnvironmentSound extends RepeatingEnvironmentSound {
    protected SurfaceEnvironmentSound(Player player) {
        super(player);
    }

    @Override
    public boolean isValidPlayerCondition() {
        return WorldHelper.isOutside(getPlayer());
    }

    @Override
    public double getVolumeScaling() {
        var cullDistance = SettingsHelper.cullSoundAboveGround();

        if (cullDistance > 0) {
            return super.getVolumeScaling() * Math.max(0.0d, 1.0d - (WorldHelper.distanceFromGround(getPlayer(), cullDistance) / cullDistance));
        } else {
            return super.getVolumeScaling();
        }
    }
}
