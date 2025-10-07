package svenhjol.charmony.tweaks.client.features.item_frame_hiding;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import svenhjol.charmony.api.core.Color;
import svenhjol.charmony.core.base.Environment;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.tweaks.common.features.item_frame_hiding.Networking.S2CAddAmethyst;
import svenhjol.charmony.tweaks.common.features.item_frame_hiding.Networking.S2CRemoveAmethyst;

public class Handlers extends Setup<ItemFrameHiding> {
    public Handlers(ItemFrameHiding feature) {
        super(feature);
    }

    public void createParticle(Level level, BlockPos pos) {
        if (!Environment.usesCharmonyServer()) return;

        var particleType = feature().common.get().registers.particleType;
        var col = new Color(DyeColor.PURPLE);

        var x = (double) pos.getX() + 0.5d;
        var y = (double) pos.getY() + 0.5d;
        var z = (double) pos.getZ() + 0.5d;

        level.addParticle(particleType, x, y, z, col.getRed(), col.getGreen(), col.getBlue());
    }

    public void addToItemFrame(S2CAddAmethyst packet, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            var player = context.player();
            var pos = packet.getPos();
            var sound = packet.getSound();
            player.level().playSound(player, pos, sound, SoundSource.PLAYERS, 1.0f, 1.0f);

            for (int i = 0; i < 3; i++) {
                createParticle(player.level(), pos);
            }
        });
    }

    public void removeFromItemFrame(S2CRemoveAmethyst packet, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            var player = context.player();
            var pos = packet.getPos();
            var sound = packet.getSound();
            player.level().playSound(player, pos, sound, SoundSource.PLAYERS, 1.0f, 1.0f);

            for (int i = 0; i < 3; i++) {
                createParticle(player.level(), pos);
            }
        });
    }
}
