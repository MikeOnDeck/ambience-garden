package svenhjol.charmony.ambient_sounds.client.features.biomes.sounds;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.Biome;
import svenhjol.charmony.ambient_sounds.client.features.biomes.BiomeSound;
import svenhjol.charmony.ambient_sounds.client.features.biomes.SurfaceBiomeSound;
import svenhjol.charmony.ambient_sounds.client.features.sound.SoundHandler;
import svenhjol.charmony.ambient_sounds.client.features.sound.SoundType;
import svenhjol.charmony.ambient_sounds.helpers.BiomeCheckHelper;
import svenhjol.charmony.core.Charmony;
import svenhjol.charmony.core.helpers.WorldHelper;

import javax.annotation.Nullable;

public class PaleGarden implements SoundType<BiomeSound> {
    public final SoundEvent daySound;
    public final SoundEvent nightSound;

    public PaleGarden() {
        daySound = SoundEvent.createVariableRangeEvent(Charmony.id("biome.pale_garden.day"));
        nightSound = SoundEvent.createVariableRangeEvent(Charmony.id("biome.pale_garden.night"));
    }

    @Override
    public void addSounds(SoundHandler<BiomeSound> handler) {
        // Day sound.
        handler.getSounds().add(new SurfaceBiomeSound(handler.getPlayer(), false) {
            @Nullable
            @Override
            public SoundEvent getSound() {
                return daySound;
            }

            @Override
            public boolean isValidPlayerCondition() {
                return super.isValidPlayerCondition() && WorldHelper.isDay(player);
            }

            @Override
            public boolean isValidBiomeCondition(Holder<Biome> holder, ResourceKey<Biome> key) {
                return BiomeCheckHelper.PALE_GARDEN.test(key);
            }

            @Override
            public float getVolume() {
                return 0.75f;
            }

            @Override
            public int getBiomeBlend() {
                return 4;
            }
        });

        // Night sound.
        handler.getSounds().add(new SurfaceBiomeSound(handler.getPlayer(), false) {
            @Nullable
            @Override
            public SoundEvent getSound() {
                return nightSound;
            }

            @Override
            public boolean isValidPlayerCondition() {
                return super.isValidPlayerCondition() && WorldHelper.isNight(player);
            }

            @Override
            public boolean isValidBiomeCondition(Holder<Biome> holder, ResourceKey<Biome> key) {
                return BiomeCheckHelper.PALE_GARDEN.test(key);
            }

            @Override
            public int getBiomeBlend() {
                return 4;
            }
        });
    }
}
