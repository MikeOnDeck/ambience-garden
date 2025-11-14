package charmony.core.client.mixins.play_sound;

import charmony.api.events.PlaySoundCallback;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Inject(
        method = "play",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/SoundBufferLibrary;getCompleteBuffer(Lnet/minecraft/resources/Identifier;)Ljava/util/concurrent/CompletableFuture;"
        )
    )
    private void hookPlayStatic(SoundInstance soundInstance, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        PlaySoundCallback.EVENT.invoker().interact((SoundEngine)(Object)this, soundInstance);
    }

    @Inject(
        method = "play",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/sounds/SoundBufferLibrary;getStream(Lnet/minecraft/resources/Identifier;Z)Ljava/util/concurrent/CompletableFuture;"
        )
    )
    private void hookPlayStreamed(SoundInstance soundInstance, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        PlaySoundCallback.EVENT.invoker().interact((SoundEngine)(Object)this, soundInstance);
    }
}
