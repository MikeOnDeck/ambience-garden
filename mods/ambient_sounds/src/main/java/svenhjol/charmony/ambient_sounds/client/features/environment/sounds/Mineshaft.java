 package svenhjol.charmony.ambient_sounds.client.features.environment.sounds;

 import net.minecraft.core.BlockPos;
 import net.minecraft.sounds.SoundEvent;
 import net.minecraft.world.level.block.Blocks;
 import org.jetbrains.annotations.Nullable;
 import svenhjol.charmony.ambient_sounds.client.features.environment.EnvironmentSound;
 import svenhjol.charmony.ambient_sounds.client.features.environment.RepeatingEnvironmentSound;
 import svenhjol.charmony.ambient_sounds.client.features.sound.SoundHandler;
 import svenhjol.charmony.ambient_sounds.client.features.sound.SoundType;
 import svenhjol.charmony.core.Charmony;

 import java.util.Optional;

 public class Mineshaft implements SoundType<EnvironmentSound> {
     public final SoundEvent sound;

     public Mineshaft() {
         sound = SoundEvent.createVariableRangeEvent(Charmony.id("environment.mineshaft"));
     }

     public void addSounds(SoundHandler<EnvironmentSound> handler) {
         handler.getSounds().add(new RepeatingEnvironmentSound(handler.getPlayer()) {
             @Override
             public boolean isValidEnvironmentCondition() {
                 // Find the closest rail block in the mineshaft. This will become the sound source.
                 Optional<BlockPos> rail = BlockPos.findClosestMatch(player.blockPosition(), 8, 16, pos -> {
                     var block = level.getBlockState(pos).getBlock();
                     return block == Blocks.RAIL;
                 });

                 // Also check for oak planks / oak fences.
                 Optional<BlockPos> wood = BlockPos.findClosestMatch(player.blockPosition(), 8, 16, pos -> {
                     var block = level.getBlockState(pos).getBlock();
                     return block == Blocks.OAK_PLANKS || block == Blocks.OAK_FENCE;
                 });

                 if (rail.isPresent() && wood.isPresent()) {
                     setPos(rail.get());
                     return true;
                 }

                 return false;
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
                 return level.random.nextInt(300) + 300;
             }

             @Override
             public float getVolume() {
                 return 0.8f;
             }
         });
     }
 }
