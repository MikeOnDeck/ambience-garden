package charmony.tweaks.client.mixins.telemetry_disable;

import charmony.tweaks.client.features.telemetry_disable.TelemetryDisable;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.telemetry.ClientTelemetryManager;
import net.minecraft.client.telemetry.TelemetryEventSender;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientTelemetryManager.class)
public class ClientTelemetryManagerMixin {
    @WrapMethod(
        method = "createEventSender"
    )
    private TelemetryEventSender hookCreateEventSender(Operation<TelemetryEventSender> original) {
        if (TelemetryDisable.disableTelemetry()) {
            return TelemetryEventSender.DISABLED;
        }
        return original.call();
    }
}
