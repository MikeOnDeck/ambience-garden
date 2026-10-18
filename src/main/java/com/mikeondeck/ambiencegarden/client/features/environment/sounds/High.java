 package com.mikeondeck.ambiencegarden.client.features.environment.sounds;

 import net.minecraft.sounds.SoundEvent;
 import net.minecraft.world.level.Level;
 import org.jetbrains.annotations.Nullable;
 import com.mikeondeck.ambiencegarden.client.features.environment.EnvironmentSound;
 import com.mikeondeck.ambiencegarden.client.features.environment.RepeatingEnvironmentSound;
 import com.mikeondeck.ambiencegarden.client.features.sound.SoundHandler;
 import com.mikeondeck.ambiencegarden.client.features.sound.SoundType;
 import com.mikeondeck.ambiencegarden.client.sound.AmbientSoundEvents;
 import com.mikeondeck.ambiencegarden.core.helpers.WorldHelper;

 public class High implements SoundType<EnvironmentSound> {
     public final SoundEvent sound;

     public High() {
         sound = AmbientSoundEvents.get("environment.high");
     }

     public void addSounds(SoundHandler<EnvironmentSound> handler) {
         handler.getSounds().add(new RepeatingEnvironmentSound(handler.getPlayer()) {
             @Override
             public boolean isValidEnvironmentCondition() {
                 var top = level.getMaxY() > 256 ? 200 : 150;
                 return level.dimension() == Level.OVERWORLD
                     && player.blockPosition().getY() > top;
             }

             @Override
             public boolean isValidPlayerCondition() {
                 return level.dimension() == Level.OVERWORLD
                     && WorldHelper.isOutside(player);
             }

             @Nullable
             @Override
             public SoundEvent getSound() {
                 return sound;
             }

             @Override
             public int getDelay() {
                 return level.random.nextInt(200) + 100;
             }

             @Override
             public float getVolume() {
                 return 0.5f;
             }
         });
     }
 }
