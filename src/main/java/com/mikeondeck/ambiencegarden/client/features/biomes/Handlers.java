package com.mikeondeck.ambiencegarden.client.features.biomes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.mikeondeck.ambiencegarden.client.features.sound.SoundHandler;
import com.mikeondeck.ambiencegarden.client.sound.SoundOverride;
import com.mikeondeck.ambiencegarden.core.base.Setup;

public class Handlers extends Setup<Biomes> {
    private Handler handler;

    public Handler activeHandler() {
        return handler;
    }

    public Handlers(Biomes feature) {
        super(feature);
    }

    public void clientTick(Minecraft client) {
        if (!feature().enabled()) {
            reload();
            return;
        }
        if (handler == null && client.player instanceof LocalPlayer player) {
            trySetupSoundHandler(player);
        }
        if (handler != null && !client.isPaused()) {
            if (SoundOverride.isActive()) {
                SoundOverride.keepAutomaticSuspended(handler);
            } else {
                handler.tick();
            }
        }
    }

    public void reload() {
        if (handler != null) {
            handler.stop();
            handler = null;
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

    public class Handler extends SoundHandler<BiomeSound> {
        public Handler(Player player) {
            super(player);
            var registers = Handlers.this.feature().registers;

            registers.badlands.addSounds(this);
            registers.beach.addSounds(this);
            registers.caves.addSounds(this);
            registers.desert.addSounds(this);
            registers.forest.addSounds(this);
            registers.icy.addSounds(this);
            registers.jungle.addSounds(this);
            registers.mountains.addSounds(this);
            registers.ocean.addSounds(this);
            registers.paleGarden.addSounds(this);
            registers.plains.addSounds(this);
            registers.river.addSounds(this);
            registers.savanna.addSounds(this);
            registers.swamp.addSounds(this);
            registers.taiga.addSounds(this);
            registers.theEnd.addSounds(this);
        }
    }
}
