package svenhjol.charmony.core.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;

@SuppressWarnings("unused")
public class CustomParticle extends SingleQuadParticle {
    private final SpriteSet spriteProvider;

    /**
     * Copypasta
     * @see net.minecraft.client.particle.GlowParticle
     */
    public CustomParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz, spriteSet.first());
        this.friction = 0.6f;
        this.speedUpWhenYMotionIsBlocked = false;
        this.spriteProvider = spriteSet;
        this.quadSize *= 0.78f;
        this.hasPhysics = false;
        this.setSpriteFromAge(spriteSet);
    }

    /**
     * Copypasta
     * @see net.minecraft.client.particle.PortalParticle
     */
    @Override
    public int getLightColor(float tint) {
        var i = super.getLightColor(tint);
        var f = (float) age / (float) lifetime;
        f *= f;
        f *= f;
        var j = i & 255;
        var k = i >> 16 & 255;
        k += (int) (f * 15.0f * 16.0f);
        if (k > 240) {
            k = 240;
        }

        return j | k << 16;
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteFromAge(spriteProvider);
    }

    @Override
    protected Layer getLayer() {
        return Layer.OPAQUE;
    }
}
