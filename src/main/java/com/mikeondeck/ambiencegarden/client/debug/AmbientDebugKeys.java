package com.mikeondeck.ambiencegarden.client.debug;

import com.mikeondeck.ambiencegarden.AmbientSoundsMod;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class AmbientDebugKeys {
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath(AmbientSoundsMod.ID, "keys")
    );

    public static KeyMapping toggleOverlay;

    private AmbientDebugKeys() {}

    public static void register() {
        toggleOverlay = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                "key.ambience_garden.debug_overlay",
                GLFW.GLFW_KEY_U,
                CATEGORY
            )
        );
    }
}
