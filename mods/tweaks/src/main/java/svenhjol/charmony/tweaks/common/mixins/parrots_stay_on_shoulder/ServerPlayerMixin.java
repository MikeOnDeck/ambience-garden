package svenhjol.charmony.tweaks.common.mixins.parrots_stay_on_shoulder;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import svenhjol.charmony.tweaks.common.features.parrots_stay_on_shoulder.ParrotsStayOnShoulder;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Entity {
    @Shadow
    private long timeEntitySatOnShoulder;

    public ServerPlayerMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    /**
     * Defer to {@link svenhjol.charmony.tweaks.common.features.parrots_stay_on_shoulder.Handlers#shouldParrotStayMounted}.
     * If check passes then return early so that entities do not dismount.
     */
    @WrapMethod(
        method = "removeEntitiesOnShoulder"
    )
    private void hookSpawnShoulderEntities(Operation<Void> original) {
        var feature = ParrotsStayOnShoulder.feature();
        var player = (Player)(Object)(this);

        if (feature.handlers.hasParrotOnAnyShoulder(player) && feature.handlers.shouldParrotStayMounted(level(), timeEntitySatOnShoulder)) {
            return;
        }

        original.call();
    }
}
