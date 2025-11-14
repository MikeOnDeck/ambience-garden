package charmony.tweaks.client.mixins.jukeboxes_stop_background_music;

import charmony.tweaks.client.features.jukeboxes_stop_background_music.JukeboxesStopBackgroundMusic;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MusicManager.class)
public class MusicManagerMixin {
    @WrapMethod(
        method = "startPlaying"
    )
    private void hookStartPlaying(Music music, Operation<Void> original) {
        if (JukeboxesStopBackgroundMusic.feature().handlers.shouldPreventMusic()) {
            return;
        }
        original.call(music);
    }
}
