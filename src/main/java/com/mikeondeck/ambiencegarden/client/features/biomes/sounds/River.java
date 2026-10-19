package com.mikeondeck.ambiencegarden.client.features.biomes.sounds;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.Biome;
import com.mikeondeck.ambiencegarden.client.features.biomes.BiomeSound;
import com.mikeondeck.ambiencegarden.client.features.biomes.SurfaceBiomeSound;
import com.mikeondeck.ambiencegarden.client.features.sound.SoundHandler;
import com.mikeondeck.ambiencegarden.client.features.sound.SoundType;
import com.mikeondeck.ambiencegarden.helpers.BiomeCheckHelper;
import com.mikeondeck.ambiencegarden.client.sound.AmbientSoundEvents;

import javax.annotation.Nullable;

public class River implements SoundType<BiomeSound> {
    public final SoundEvent sound;

    public River() {
        sound = AmbientSoundEvents.get("biome.river");
    }

    @Override
    public void addSounds(SoundHandler<BiomeSound> handler) {
        handler.getSounds().add(new SurfaceBiomeSound(handler.getPlayer(), true) {
            @Nullable
            @Override
            public SoundEvent getSound() {
                return sound;
            }

            @Override
            public boolean isValidBiomeCondition(Holder<Biome> holder, ResourceKey<Biome> key) {
                return BiomeCheckHelper.RIVER.test(holder, key);
            }
        });
    }
}
