package svenhjol.charmony.ambient_sounds.client.features.biomes.sounds;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.Biome;
import svenhjol.charmony.ambient_sounds.client.features.biomes.BiomeSound;
import svenhjol.charmony.ambient_sounds.client.features.sound.SoundHandler;
import svenhjol.charmony.ambient_sounds.client.features.sound.SoundType;
import svenhjol.charmony.ambient_sounds.helpers.BiomeCheckHelper;
import svenhjol.charmony.core.Charmony;

import javax.annotation.Nullable;

public class TheEnd implements SoundType<BiomeSound> {
    public final SoundEvent sound;

    public TheEnd() {
        sound = SoundEvent.createVariableRangeEvent(Charmony.id("biome.the_end"));
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
