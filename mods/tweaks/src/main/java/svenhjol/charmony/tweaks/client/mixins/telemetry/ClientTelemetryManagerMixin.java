package svenhjol.charmony.tweaks.client.mixins.telemetry;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.telemetry.ClientTelemetryManager;
import net.minecraft.client.telemetry.TelemetryEventSender;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charmony.tweaks.client.features.telemetry.Telemetry;

@Mixin(ClientTelemetryManager.class)
public class ClientTelemetryManagerMixin {
    @WrapMethod(
        method = "createEventSender"
    )
    private TelemetryEventSender hookCreateEventSender(Operation<TelemetryEventSender> original) {
        if (Telemetry.disableTelemetry()) {
            return TelemetryEventSender.DISABLED;
        }
        return original.call();
    }
}
