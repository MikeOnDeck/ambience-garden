 package com.mikeondeck.ambiencegarden.client.features.environment.sounds;

 import net.minecraft.sounds.SoundEvent;
 import org.jetbrains.annotations.Nullable;
 import com.mikeondeck.ambiencegarden.client.features.environment.EnvironmentSound;
 import com.mikeondeck.ambiencegarden.client.features.environment.RepeatingEnvironmentSound;
 import com.mikeondeck.ambiencegarden.client.features.sound.SoundHandler;
 import com.mikeondeck.ambiencegarden.client.features.sound.SoundType;
 import com.mikeondeck.ambiencegarden.helpers.BiomeCheckHelper;
 import com.mikeondeck.ambiencegarden.core.Charmony;
 import com.mikeondeck.ambiencegarden.core.helpers.WorldHelper;

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
