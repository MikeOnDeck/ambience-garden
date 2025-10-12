package charmony.core.client;

import net.minecraft.client.particle.ParticleResources;
import net.minecraft.core.particles.SimpleParticleType;

public record DeferredParticle(
    SimpleParticleType type,
    ParticleResources.SpriteParticleRegistration<SimpleParticleType> registration
) { }
