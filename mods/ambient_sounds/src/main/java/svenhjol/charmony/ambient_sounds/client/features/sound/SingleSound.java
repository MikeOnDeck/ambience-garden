package svenhjol.charmony.ambient_sounds.client.features.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import svenhjol.charmony.ambient_sounds.helpers.SettingsHelper;

public class SingleSound extends AbstractTickableSoundInstance {
    private final Player player;

    public SingleSound(Player player, SoundEvent sound, float volume) {
        this(player, sound, volume, 1.0f, null);
    }

    public SingleSound(Player player, SoundEvent sound, float volume, float pitch, @Nullable BlockPos pos) {
        super(sound, SettingsHelper.soundSource(), RandomSource.create());

        this.player = player;
        this.looping = false;
        this.delay = 0;
        this.pitch = pitch;
        this.volume = volume;

        if (pos != null) {
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
        } else {
            this.relative = true;
        }
    }

    @Override
    public void tick() {
        if (player == null || !player.isAlive()) {
            stopped = true;
        }
    }
}
