package svenhjol.charmony.ambient_sounds.client.features.biomes.sounds;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import svenhjol.charmony.ambient_sounds.client.features.biomes.BiomeSound;
import svenhjol.charmony.ambient_sounds.client.features.sound.SoundHandler;
import svenhjol.charmony.ambient_sounds.client.features.sound.SoundType;
import svenhjol.charmony.core.Charmony;

import javax.annotation.Nullable;

public class Caves implements SoundType<BiomeSound> {
    public final SoundEvent deepDark;
    public final SoundEvent dripstone;
    public final SoundEvent lush;

    public Caves() {
        deepDark = SoundEvent.createVariableRangeEvent(Charmony.id("biome.caves.deep_dark"));
        dripstone = SoundEvent.createVariableRangeEvent(Charmony.id("biome.caves.dripstone"));
        lush = SoundEvent.createVariableRangeEvent(Charmony.id("biome.caves.lush"));
    }

    @Override
    public void addSounds(SoundHandler<BiomeSound> handler) {
        // Deep dark caves.
        handler.getSounds().add(new BiomeSound(handler.getPlayer()) {
            @Override
            public boolean isValidBiomeCondition(Holder<Biome> holder, ResourceKey<Biome> key) {
                return key.equals(Biomes.DEEP_DARK);
            }

            @Override
            public boolean isValidPlayerCondition() {
                return true;
            }

            @Nullable
            @Override
            public SoundEvent getSound() {
                return deepDark;
            }

            @Override
            public int getBiomeBlend() {
                return 4;
            }
        });

        // Dripstone caves.
        handler.getSounds().add(new BiomeSound(handler.getPlayer()) {
            @Override
            public boolean isValidBiomeCondition(Holder<Biome> holder, ResourceKey<Biome> key) {
                return key.equals(Biomes.DRIPSTONE_CAVES);
            }

            @Override
            public boolean isValidPlayerCondition() {
                return true;
            }

            @Nullable
            @Override
            public SoundEvent getSound() {
                return dripstone;
            }

            @Override
            public float getVolume() {
                return 0.6f;
            }

            @Override
            public int getBiomeBlend() {
                return 4;
            }
        });

        // Lush caves.
        handler.getSounds().add(new BiomeSound(handler.getPlayer()) {
            @Override
            public boolean isValidBiomeCondition(Holder<Biome> holder, ResourceKey<Biome> key) {
                return key.equals(Biomes.LUSH_CAVES);
            }

            @Override
            public boolean isValidPlayerCondition() {
                return true;
            }

            @Nullable
            @Override
            public SoundEvent getSound() {
                return lush;
            }
        });
    }
}
