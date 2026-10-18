package com.mikeondeck.ambiencegarden.client.features.biomes.sounds;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.Biome;
import com.mikeondeck.ambiencegarden.client.features.biomes.BiomeSound;
import com.mikeondeck.ambiencegarden.client.features.sound.SoundHandler;
import com.mikeondeck.ambiencegarden.client.features.sound.SoundType;
import com.mikeondeck.ambiencegarden.helpers.BiomeCheckHelper;
import com.mikeondeck.ambiencegarden.client.sound.AmbientSoundEvents;

import javax.annotation.Nullable;

public class TheEnd implements SoundType<BiomeSound> {
    public final SoundEvent sound;

    public TheEnd() {
        sound = AmbientSoundEvents.get("biome.the_end");
    }

    @Override
    public void addSounds(SoundHandler<BiomeSound> handler) {
        handler.getSounds().add(new BiomeSound(handler.getPlayer()) {
            @Override
            public boolean isValidBiomeCondition(Holder<Biome> holder, ResourceKey<Biome> key) {
                return BiomeCheckHelper.THE_END.test(holder);
            }

            @Override
            public boolean isValidPlayerCondition() {
                return true;
            }

            @Nullable
            @Override
            public SoundEvent getSound() {
                return sound;
            }
        });
    }
}
