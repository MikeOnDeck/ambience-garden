package charmony.tweaks.client.mixins.telemetry_disable;

import charmony.tweaks.client.features.telemetry_disable.TelemetryDisable;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.realmsclient.RealmsAvailability;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.CompletableFuture;

@Mixin(RealmsAvailability.class)
public class RealmsAvailabilityMixin {
    @WrapMethod(
        method = "check"
    )
    private static CompletableFuture<RealmsAvailability.Result> hookCheck(Operation<CompletableFuture<RealmsAvailability.Result>> original) {
        if (TelemetryDisable.disableDevEnvironmentConnections()) {
            return CompletableFuture.supplyAsync(() -> new RealmsAvailability.Result(RealmsAvailability.Type.INCOMPATIBLE_CLIENT));
        }
        return original.call();
    }
}
