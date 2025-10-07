 package svenhjol.charmony.ambient_sounds.client.features.environment.sounds;

 import net.minecraft.core.BlockPos;
 import net.minecraft.sounds.SoundEvent;
 import net.minecraft.world.entity.monster.Monster;
 import net.minecraft.world.level.block.Blocks;
 import net.minecraft.world.phys.AABB;
 import org.jetbrains.annotations.Nullable;
 import svenhjol.charmony.ambient_sounds.client.features.environment.EnvironmentSound;
 import svenhjol.charmony.ambient_sounds.client.features.environment.RepeatingEnvironmentSound;
 import svenhjol.charmony.ambient_sounds.client.features.sound.SoundHandler;
 import svenhjol.charmony.ambient_sounds.client.features.sound.SoundType;
 import svenhjol.charmony.core.Charmony;
 import svenhjol.charmony.core.helpers.WorldHelper;

 import java.util.List;

 public class Mansion implements SoundType<EnvironmentSound> {
     public final SoundEvent sound;

     public Mansion() {
         sound = SoundEvent.createVariableRangeEvent(Charmony.id("environment.mansion"));
     }

     public void addSounds(SoundHandler<EnvironmentSound> handler) {
         handler.getSounds().add(new RepeatingEnvironmentSound(handler.getPlayer()) {
             @Override
             public boolean isValidEnvironmentCondition() {
                 var bb = new AABB(player.blockPosition()).inflate(10);
                 List<Monster> monsters = level.getEntitiesOfClass(Monster.class, bb);

                 var optBlock1 = BlockPos.findClosestMatch(player.blockPosition(), 8, 8, pos -> {
                     var block = level.getBlockState(pos).getBlock();
                     return block == Blocks.DARK_OAK_PLANKS;
                 });

                 var optBlock2 = BlockPos.findClosestMatch(player.blockPosition(), 8, 8, pos -> {
                     var block = level.getBlockState(pos).getBlock();
                     return block == Blocks.BIRCH_PLANKS;
                 });

                 if (optBlock1.isPresent() && optBlock2.isPresent()) {
                     // Get a hostile mob's location as the source of the sound.
                     var optMonster = monsters.stream().findAny();
                     if (optMonster.isPresent()) {
                         setPos(optMonster.get().blockPosition());
                         return true;
                     }
                 }

                 return false;
             }

             @Override
             public boolean isValidPlayerCondition() {
                 return !WorldHelper.isBelowSeaLevel(player);
             }

             @Nullable
             @Override
             public SoundEvent getSound() {
                 return sound;
             }

             @Override
             public int getDelay() {
                 return level.random.nextInt(200) + 400;
             }

             @Override
             public float getVolume() {
                 return 0.82f;
             }
         });
     }
 }
