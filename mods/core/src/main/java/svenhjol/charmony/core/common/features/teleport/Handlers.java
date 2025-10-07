package svenhjol.charmony.core.common.features.teleport;

import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.core.base.Setup;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class Handlers extends Setup<Teleport> {
    private final Map<UUID, Teleporter> teleports = new HashMap<>();

    public Handlers(Teleport feature) {
        super(feature);
    }

    public void playerTick(Player player) {
        var uuid = player.getUUID();

        if (teleports.containsKey(uuid)) {
            var teleport = teleports.get(uuid);
            if (teleport.isValid()) {
                teleport.tick();
            } else {
                log().debug("Removing completed teleport for " + uuid);
                teleports.remove(uuid);
            }
        }
    }

    public void addTeleport(Player player, Teleporter teleport) {
        if (player.level().isClientSide()) return;
        teleports.put(player.getUUID(), teleport);
    }
}
