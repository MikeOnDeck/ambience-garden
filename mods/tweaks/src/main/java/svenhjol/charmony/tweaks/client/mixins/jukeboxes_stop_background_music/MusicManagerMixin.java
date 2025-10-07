package svenhjol.charmony.tweaks.client.mixins.jukeboxes_stop_background_music;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charmony.tweaks.client.features.jukeboxes_stop_background_music.JukeboxesStopBackgroundMusic;

@Mixin(MusicManager.class)
public class MusicManagerMixin {
    @WrapMethod(
        method = "startPlaying"
    )
    private void hookStartPlaying(MusicInfo musicInfo, Operation<Void> original) {
        if (JukeboxesStopBackgroundMusic.feature().handlers.shouldPreventMusic()) {
            return;
        }
        original.call(musicInfo);
    }
}
