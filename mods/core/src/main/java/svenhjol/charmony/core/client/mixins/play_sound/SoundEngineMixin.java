package svenhjol.charmony.core.client.mixins.play_sound;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charmony.api.events.PlaySoundCallback;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Inject(
        method = "play",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/SoundBufferLibrary;getCompleteBuffer(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/concurrent/CompletableFuture;"
        )
    )
    private void hookPlayStatic(SoundInstance soundInstance, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        PlaySoundCallback.EVENT.invoker().interact((SoundEngine)(Object)this, soundInstance);
    }

    @Inject(
        method = "play",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/SoundBufferLibrary;getStream(Lnet/minecraft/resources/ResourceLocation;Z)Ljava/util/concurrent/CompletableFuture;"
        )
    )
    private void hookPlayStreamed(SoundInstance soundInstance, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        PlaySoundCallback.EVENT.invoker().interact((SoundEngine)(Object)this, soundInstance);
    }
}
