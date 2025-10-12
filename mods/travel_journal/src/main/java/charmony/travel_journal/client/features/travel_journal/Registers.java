package charmony.travel_journal.client.features.travel_journal;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import charmony.api.events.HudDisplayCallback;
import charmony.core.base.Setup;
import charmony.core.client.ClientRegistry;
import charmony.api.events.ClientLoginPlayerCallback;
import charmony.api.events.PlayerTickCallback;
import charmony.travel_journal.common.features.travel_journal.Networking.S2CSendBookmarkToPlayer;

public class Registers extends Setup<TravelJournal> {
    public KeyMapping openJournalKey;
    public KeyMapping makeBookmarkKey;
    public final HudRenderer hudRenderer;

    public Registers(TravelJournal feature) {
        super(feature);
        hudRenderer = new HudRenderer();
    }

    @Override
    public Runnable boot() {
        return () -> {
            var registry = ClientRegistry.forFeature(feature());

            registry.packetReceiver(S2CSendBookmarkToPlayer.TYPE,
                feature().handlers::handleSendBookmarkToPlayerPacket);

            openJournalKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.charmony.open_journal",
                GLFW.GLFW_KEY_J,
                KeyMapping.Category.MISC));
            makeBookmarkKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.charmony.make_bookmark",
                GLFW.GLFW_KEY_BACKSLASH,
                KeyMapping.Category.MISC));

            ClientLoginPlayerCallback.EVENT.register(feature().handlers::clientLogin);
            HudDisplayCallback.EVENT.register(feature().handlers::hudRender);
            PlayerTickCallback.EVENT.register(feature().handlers::playerTick);
        };
    }
}
