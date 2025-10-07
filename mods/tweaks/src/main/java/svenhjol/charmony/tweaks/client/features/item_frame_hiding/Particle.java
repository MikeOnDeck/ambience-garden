package svenhjol.charmony.tweaks.client.features.item_frame_hiding;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import svenhjol.charmony.core.client.CustomParticle;

@SuppressWarnings("ConstantConditions")
public class Particle implements ParticleProvider<SimpleParticleType> {
    private static final RandomSource RANDOM = RandomSource.create();
    private final SpriteSet sprite;

    public Particle(SpriteSet sprite) {
        this.sprite = sprite;
    }

    public net.minecraft.client.particle.Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double r, double g, double b, RandomSource random) {
        var particle = new CustomParticle(level, x, y, z,
            0.5d - RANDOM.nextDouble(), 0.5d - RANDOM.nextDouble(), 0.5d - RANDOM.nextDouble(), this.sprite);
        particle.setLifetime(10 + RANDOM.nextInt(10));
        particle.setColor((float)r, (float)g, (float)b);
        particle.setAlpha((RANDOM.nextFloat() * 0.2f) + 0.8f);
        particle.friction = 0.8f; // some multiplier for velocity, idk
        particle.speedUpWhenYMotionIsBlocked = true; // idk
        return particle;
    }
}
