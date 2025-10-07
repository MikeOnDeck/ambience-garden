package svenhjol.charmony.tweaks.client.features.crafting_table_nearby;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import svenhjol.charmony.api.events.HudDisplayCallback;
import svenhjol.charmony.core.base.Setup;

public class Registers extends Setup<CraftingTableNearby> {
    public final KeyMapping openCraftingKey;
    public final HudRenderer hudRenderer;

    public Registers(CraftingTableNearby feature) {
        super(feature);

        this.hudRenderer = new HudRenderer();
        this.openCraftingKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.charmony.openCrafting",
            GLFW.GLFW_KEY_V,
            KeyMapping.Category.MISC));
    }

    @Override
    public Runnable boot() {
        return () -> {
            ClientTickEvents.END_CLIENT_TICK.register(feature().handlers::clientTick);
            HudDisplayCallback.EVENT.register(feature().handlers::hudRender);
        };
    }
}
