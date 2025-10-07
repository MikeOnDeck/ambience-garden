package svenhjol.charmony.ambient_sounds.client.features.biomes;

import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.ambient_sounds.helpers.SettingsHelper;
import svenhjol.charmony.core.helpers.WorldHelper;

public abstract class SurfaceBiomeSound extends BiomeSound {
    protected boolean playWhenThundering;

    protected SurfaceBiomeSound(Player player, boolean playWhenThundering) {
        super(player);
        this.playWhenThundering = playWhenThundering;
    }

    @Override
    public boolean isValidPlayerCondition() {
        if (WorldHelper.isThundering(getPlayer()) && !playWhenThundering) {
            return false;
        }
        return WorldHelper.isOutside(getPlayer());
    }

    @Override
    public double getVolumeScaling() {
        var cullDistance = SettingsHelper.cullSoundAboveGround();

        if (cullDistance > 0) {
            var distanceFromGround = WorldHelper.distanceFromGround(getPlayer(), cullDistance);
            var multiplier = 1.0d - (distanceFromGround / cullDistance);
            return super.getVolumeScaling() * Math.max(0.0d, multiplier);
        } else {
            return super.getVolumeScaling();
        }
    }
}
