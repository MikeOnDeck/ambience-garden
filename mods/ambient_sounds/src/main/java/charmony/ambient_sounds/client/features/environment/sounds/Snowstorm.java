 package charmony.ambient_sounds.client.features.environment.sounds;

 import net.minecraft.sounds.SoundEvent;
 import org.jetbrains.annotations.Nullable;
 import charmony.ambient_sounds.client.features.environment.EnvironmentSound;
 import charmony.ambient_sounds.client.features.environment.RepeatingEnvironmentSound;
 import charmony.ambient_sounds.client.features.sound.SoundHandler;
 import charmony.ambient_sounds.client.features.sound.SoundType;
 import charmony.ambient_sounds.helpers.BiomeCheckHelper;
 import charmony.core.Charmony;
 import charmony.core.helpers.WorldHelper;

 public class Snowstorm implements SoundType<EnvironmentSound> {
     public final SoundEvent sound;

     public Snowstorm() {
         sound = SoundEvent.createVariableRangeEvent(Charmony.id("environment.snowstorm"));
     }

     public void addSounds(SoundHandler<EnvironmentSound> handler) {
         handler.getSounds().add(new RepeatingEnvironmentSound(handler.getPlayer()) {
             @Override
             public boolean isValidEnvironmentCondition() {
                 var holder = getBiomeHolder(player.blockPosition());
                 return BiomeCheckHelper.ICY.test(holder);
             }

             @Override
             public boolean isValidPlayerCondition() {
                 return WorldHelper.isOutside(player)
                     && getLevel().isThundering();
             }

             @Nullable
             @Override
             public SoundEvent getSound() {
                 return sound;
             }

             @Override
             public int getDelay() {
                 return level.random.nextInt(250) + 250;
             }

             @Override
             public float getVolume() {
                 return 0.9f;
             }
         });
     }
 }
