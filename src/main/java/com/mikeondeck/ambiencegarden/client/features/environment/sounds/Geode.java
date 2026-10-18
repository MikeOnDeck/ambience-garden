 package com.mikeondeck.ambiencegarden.client.features.environment.sounds;

 import net.minecraft.core.BlockPos;
 import net.minecraft.sounds.SoundEvent;
 import net.minecraft.world.level.block.AmethystBlock;
 import net.minecraft.world.level.block.Blocks;
 import org.jetbrains.annotations.Nullable;
 import com.mikeondeck.ambiencegarden.client.features.environment.EnvironmentSound;
 import com.mikeondeck.ambiencegarden.client.features.environment.RepeatingEnvironmentSound;
 import com.mikeondeck.ambiencegarden.client.features.sound.SoundHandler;
 import com.mikeondeck.ambiencegarden.client.features.sound.SoundType;
 import com.mikeondeck.ambiencegarden.client.sound.AmbientSoundEvents;
 import com.mikeondeck.ambiencegarden.core.helpers.WorldHelper;

 import java.util.Optional;

 public class Geode implements SoundType<EnvironmentSound> {
     public final SoundEvent sound;

     public Geode() {
         sound = AmbientSoundEvents.get("environment.geode");
     }

     public void addSounds(SoundHandler<EnvironmentSound> handler) {
         handler.getSounds().add(new RepeatingEnvironmentSound(handler.getPlayer()) {
             @Override
             public boolean isValidEnvironmentCondition() {
                 Optional<BlockPos> optAmethyst = BlockPos.findClosestMatch(player.blockPosition(), 12, 8, pos -> {
                     var block = level.getBlockState(pos).getBlock();
                     return block instanceof AmethystBlock;
                 });

                 Optional<BlockPos> optSmoothBasalt = BlockPos.findClosestMatch(player.blockPosition(), 12, 8, pos -> {
                     var block = level.getBlockState(pos).getBlock();
                     return block == Blocks.SMOOTH_BASALT;
                 });

                 if (optAmethyst.isPresent() && optSmoothBasalt.isPresent()) {
                     setPos(optAmethyst.get());
                     return true;
                 }

                 return false;
             }

             @Override
             public boolean isValidPlayerCondition() {
                 return !WorldHelper.isOutside(player)
                     && WorldHelper.isBelowSeaLevel(player);
             }

             @Nullable
             @Override
             public SoundEvent getSound() {
                 return sound;
             }

             @Override
             public int getDelay() {
                 return level.random.nextInt(800) + 800;
             }

             @Override
             public float getVolume() {
                 return 0.5f;
             }

             @Override
             public float getPitch() {
                 return 0.9f + (0.2f * level.random.nextFloat());
             }
         });
     }
 }
