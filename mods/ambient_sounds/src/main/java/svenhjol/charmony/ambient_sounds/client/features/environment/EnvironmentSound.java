package svenhjol.charmony.ambient_sounds.client.features.environment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.ambient_sounds.client.features.sound.SoundInstance;
import svenhjol.charmony.ambient_sounds.helpers.SettingsHelper;

public abstract class EnvironmentSound implements SoundInstance {
    protected Minecraft client;
    protected Player player;
    protected ClientLevel level;
    protected boolean isValid;
    protected boolean playUnderWater = false;

    public EnvironmentSound(Player player) {
        this.client = Minecraft.getInstance();
        this.player = player;
        this.level = (ClientLevel) player.level();
    }

    @Override
    public ClientLevel getLevel() {
        return level;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void updatePlayer(Player player) {
        this.player = player;
        this.level = (ClientLevel) player.level();
    }

    public abstract boolean isValidEnvironmentCondition();

    @Override
    public boolean isValid() {

        // Initial filters.
        if (client.level == null || level == null) return false;
        if (!player.isAlive()) return false;
        if (player.isUnderWater() && !playUnderWater) return false;

        return isValidEnvironmentCondition()
            && isValidPlayerCondition();
    }

    @Override
    public double getVolumeScaling() {
        return SettingsHelper.environmentVolumeScaling();
    }
}
