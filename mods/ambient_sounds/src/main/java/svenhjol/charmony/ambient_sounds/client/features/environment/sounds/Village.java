 package svenhjol.charmony.ambient_sounds.client.features.environment.sounds;

 import net.minecraft.sounds.SoundEvent;
 import net.minecraft.world.entity.npc.Villager;
 import net.minecraft.world.phys.AABB;
 import org.jetbrains.annotations.Nullable;
 import svenhjol.charmony.ambient_sounds.client.features.environment.EnvironmentSound;
 import svenhjol.charmony.ambient_sounds.client.features.environment.SurfaceEnvironmentSound;
 import svenhjol.charmony.ambient_sounds.client.features.sound.SoundHandler;
 import svenhjol.charmony.ambient_sounds.client.features.sound.SoundType;
 import svenhjol.charmony.core.Charmony;
 import svenhjol.charmony.core.helpers.WorldHelper;

 import java.util.List;

 public class Village implements SoundType<EnvironmentSound> {
     public final SoundEvent sound;

     public Village() {
         sound = SoundEvent.createVariableRangeEvent(Charmony.id("environment.village"));
     }

     public void addSounds(SoundHandler<EnvironmentSound> handler) {
         handler.getSounds().add(new SurfaceEnvironmentSound(handler.getPlayer()) {
             @Override
             public boolean isValidEnvironmentCondition() {
                 var bb = new AABB(player.blockPosition()).inflate(32);
                 List<Villager> villagers = level.getEntitiesOfClass(Villager.class, bb);

                 if (villagers.size() >= 2) {
                     var villager = villagers.get(player.getRandom().nextInt(villagers.size()));
                     setPos(villager.blockPosition());
                     return true;
                 }

                 return false;
             }

             @Override
             public boolean isValidPlayerCondition() {
                 return super.isValidPlayerCondition()
                     && !WorldHelper.isNight(player);
             }

             @Nullable
             @Override
             public SoundEvent getSound() {
                 return sound;
             }

             @Override
             public int getDelay() {
                 return level.random.nextInt(400) + 320;
             }

             @Override
             public float getVolume() {
                 return 0.82F;
             }
         });
     }
 }
