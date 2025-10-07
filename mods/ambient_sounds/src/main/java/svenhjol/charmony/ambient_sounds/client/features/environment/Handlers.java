package svenhjol.charmony.ambient_sounds.client.features.environment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import svenhjol.charmony.ambient_sounds.client.features.sound.SoundHandler;
import svenhjol.charmony.core.base.Setup;

public class Handlers extends Setup<Environment> {
    private Handler handler;

    public Handlers(Environment feature) {
        super(feature);
    }

    public void clientTick(Minecraft client) {
        if (handler != null && !client.isPaused()) {
            handler.tick();
        }
    }

    public void clientEntityLeave(Entity entity, Level level) {
        if (entity instanceof LocalPlayer && handler != null) {
            handler.stop();
        }
    }

    public void clientEntityJoin(Entity entity, Level level) {
        if (entity instanceof LocalPlayer player) {
            trySetupSoundHandler(player);
        }
    }

    public void trySetupSoundHandler(Player player) {
        if (!(player instanceof LocalPlayer)) return;

        handler = new Handler(player);
        handler.updatePlayer(player);
    }

    public class Handler extends SoundHandler<EnvironmentSound> {
        public Handler(@NotNull Player player) {
            super(player);
            var registers = feature().registers;

            if (feature().alien()) registers.alien.addSounds(this);
            if (feature().bleak()) registers.bleak.addSounds(this);
            if (feature().caveDrone()) registers.caveDrone.addSounds(this);
            if (feature().caveDepth()) registers.caveDepth.addSounds(this);
            if (feature().deepslate()) registers.deepslate.addSounds(this);
            if (feature().dry()) registers.dry.addSounds(this);
            if (feature().geode()) registers.geode.addSounds(this);
            if (feature().gravel()) registers.gravel.addSounds(this);
            if (feature().high()) registers.high.addSounds(this);
            if (feature().mansion()) registers.mansion.addSounds(this);
            if (feature().mineshaft()) registers.mineshaft.addSounds(this);
            if (feature().nightPlains()) registers.nightPlains.addSounds(this);
            if (feature().snowstorm()) registers.snowstorm.addSounds(this);
            if (feature().undergroundWater()) registers.undergroundWater.addSounds(this);
            if (feature().village()) registers.village.addSounds(this);
        }
    }
}
