 package charmony.ambient_sounds.client.features.environment.sounds;

 import net.minecraft.sounds.SoundEvent;
 import org.jetbrains.annotations.Nullable;
 import charmony.ambient_sounds.client.features.environment.EnvironmentSound;
 import charmony.ambient_sounds.client.features.environment.SurfaceEnvironmentSound;
 import charmony.ambient_sounds.client.features.sound.SoundHandler;
 import charmony.ambient_sounds.client.features.sound.SoundType;
 import charmony.ambient_sounds.helpers.BiomeCheckHelper;
 import charmony.core.Charmony;
 import charmony.core.helpers.WorldHelper;

 public class Bleak implements SoundType<EnvironmentSound> {
     public final SoundEvent sound;

     public Bleak() {
         sound = SoundEvent.createVariableRangeEvent(Charmony.id("environment.bleak"));
     }

     public void addSounds(SoundHandler<EnvironmentSound> handler) {
         handler.getSounds().add(new SurfaceEnvironmentSound(handler.getPlayer()) {
             @Override
             public boolean isValidEnvironmentCondition() {
                 var holder = getBiomeHolder(player.blockPosition());
                 return BiomeCheckHelper.ICY.test(holder)
                     || BiomeCheckHelper.MOUNTAIN.test(holder);
             }

             @Override
             public boolean isValidPlayerCondition() {
                 return !WorldHelper.isNight(player)
                     && WorldHelper.isOutside(player)
                     && !WorldHelper.isBelowSeaLevel(player);
             }

             @Nullable
             @Override
             public SoundEvent getSound() {
                 return sound;
             }

             @Override
             public int getDelay() {
                 return level.random.nextInt(750) + 500;
             }

             @Override
             public float getVolume() {
                 return 0.5f;
             }
         });
     }
 }
