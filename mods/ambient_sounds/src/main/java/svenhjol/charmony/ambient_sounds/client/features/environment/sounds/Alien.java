 package svenhjol.charmony.ambient_sounds.client.features.environment.sounds;

 import net.minecraft.sounds.SoundEvent;
 import org.jetbrains.annotations.Nullable;
 import svenhjol.charmony.ambient_sounds.client.features.environment.EnvironmentSound;
 import svenhjol.charmony.ambient_sounds.client.features.environment.RepeatingEnvironmentSound;
 import svenhjol.charmony.ambient_sounds.client.features.sound.SoundHandler;
 import svenhjol.charmony.ambient_sounds.client.features.sound.SoundType;
 import svenhjol.charmony.ambient_sounds.helpers.BiomeCheckHelper;
 import svenhjol.charmony.core.Charmony;

 public class Alien implements SoundType<EnvironmentSound> {
    public final SoundEvent sound;

    public Alien() {
        sound = SoundEvent.createVariableRangeEvent(Charmony.id("environment.alien"));
    }

    public void addSounds(SoundHandler<EnvironmentSound> handler) {
        handler.getSounds().add(new RepeatingEnvironmentSound(handler.getPlayer()) {
            @Override
            public boolean isValidEnvironmentCondition() {
                var holder = getBiomeHolder(player.blockPosition());
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

            @Override
            public int getDelay() {
                return level.random.nextInt(400) + 300;
            }

            @Override
            public float getVolume() {
                return 0.85f;
            }
        });
    }
 }
