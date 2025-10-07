package svenhjol.charmony.tweaks.common.features.item_frame_hiding;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import svenhjol.charmony.core.Charmony;
import svenhjol.charmony.core.base.Setup;

public class Registers extends Setup<ItemFrameHiding> {
    public final SimpleParticleType particleType;

    public Registers(ItemFrameHiding feature) {
        super(feature);

        // TODO: make common registry method for this and for ChorusNetwork mod.
        particleType = Registry.register(BuiltInRegistries.PARTICLE_TYPE, Charmony.id("apply_amethyst"), new ParticleType());
    }

    @Override
    public Runnable boot() {
        return () -> {
            // Server-to-client packets
            PayloadTypeRegistry.playS2C().register(Networking.S2CAddAmethyst.TYPE, Networking.S2CAddAmethyst.CODEC);
            PayloadTypeRegistry.playS2C().register(Networking.S2CRemoveAmethyst.TYPE, Networking.S2CRemoveAmethyst.CODEC);

            UseEntityCallback.EVENT.register(feature().handlers::entityUse);
            AttackEntityCallback.EVENT.register(feature().handlers::entityAttack);
        };
    }
}
