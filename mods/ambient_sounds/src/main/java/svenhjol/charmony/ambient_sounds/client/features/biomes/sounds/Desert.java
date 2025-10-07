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

public class Desert implements SoundType<BiomeSound> {
    public final SoundEvent daySound;
    public final SoundEvent nightSound;

    public Desert() {
        daySound = SoundEvent.createVariableRangeEvent(Charmony.id("biome.desert.day"));
        nightSound = SoundEvent.createVariableRangeEvent(Charmony.id("biome.desert.night"));
    }

    @Override
    public void addSounds(SoundHandler<BiomeSound> handler) {
        // Day sound.
        handler.getSounds().add(new SurfaceBiomeSound(handler.getPlayer(), true) {
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
                return BiomeCheckHelper.DESERT.test(holder);
            }
        });

        // Night sound.
        handler.getSounds().add(new SurfaceBiomeSound(handler.getPlayer(), true) {
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
                return BiomeCheckHelper.DESERT.test(holder);
            }
        });
    }
}
