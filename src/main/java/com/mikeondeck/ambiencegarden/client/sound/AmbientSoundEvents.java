package com.mikeondeck.ambiencegarden.client.sound;

import com.mikeondeck.ambiencegarden.core.base.Log;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registers every ambient sound event in the {@code ambience_garden} namespace
 * so it lines up with {@code assets/ambience_garden/sounds.json}.
 */
public final class AmbientSoundEvents {
    private static final Log LOG = new Log(AmbientSoundIds.NAMESPACE, "Sounds");
    private static final Map<String, SoundEvent> EVENTS = new LinkedHashMap<>();

    private AmbientSoundEvents() {}

    public static void registerAll() {
        for (String path : AmbientSoundIds.PATHS) {
            var id = Identifier.fromNamespaceAndPath(AmbientSoundIds.NAMESPACE, path);
            SoundEvent event = BuiltInRegistries.SOUND_EVENT.containsKey(id)
                ? BuiltInRegistries.SOUND_EVENT.getValue(id)
                : Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
            EVENTS.put(path, event);
        }
        LOG.info("Registered " + EVENTS.size() + " ambient sound events in namespace " + AmbientSoundIds.NAMESPACE);
    }

    public static SoundEvent get(String path) {
        var event = EVENTS.get(path);
        if (event == null) {
            throw new IllegalStateException("Ambient sound event was not registered: " + AmbientSoundIds.NAMESPACE + ":" + path);
        }
        return event;
    }

    public static Collection<SoundEvent> registered() {
        return EVENTS.values();
    }
}
