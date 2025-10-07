package svenhjol.charmony.tweaks.client.mixins.telemetry;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charmony.tweaks.client.features.telemetry.Telemetry;

@Mixin(WorldOpenFlows.class)
public abstract class WorldOpenFlowsMixin {
    @WrapMethod(
        method = "confirmWorldCreation"
    )
    private static void hookConfirmWorldCreation(Minecraft minecraft, CreateWorldScreen createWorldScreen, Lifecycle lifecycle, Runnable runnable, boolean bl, Operation<Void> original) {
        if (Telemetry.disableExperimental() && lifecycle == Lifecycle.experimental()) {
            runnable.run();
            return;
        }
        original.call(minecraft, createWorldScreen, lifecycle, runnable, bl);
    }
}
