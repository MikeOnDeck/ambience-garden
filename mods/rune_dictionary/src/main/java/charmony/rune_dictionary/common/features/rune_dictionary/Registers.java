package charmony.rune_dictionary.common.features.rune_dictionary;

import charmony.api.core.Side;
import charmony.core.base.Setup;
import charmony.core.common.CommonRegistry;
import charmony.rune_dictionary.common.features.rune_dictionary.Networking.C2SRequestDictionary;
import charmony.rune_dictionary.common.features.rune_dictionary.Networking.C2SRequestKnowledge;
import charmony.rune_dictionary.common.features.rune_dictionary.Networking.S2CDictionary;
import charmony.rune_dictionary.common.features.rune_dictionary.Networking.S2CKnowledge;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class Registers extends Setup<RuneDictionary> {
    public Registers(RuneDictionary feature) {
        super(feature);

        var registry = CommonRegistry.forFeature(feature());

        // Packet registration.
        registry.packetSender(Side.Common, S2CDictionary.TYPE, S2CDictionary.CODEC);
        registry.packetSender(Side.Common, S2CKnowledge.TYPE, S2CKnowledge.CODEC);
        registry.packetSender(Side.Client, C2SRequestKnowledge.TYPE, C2SRequestKnowledge.CODEC);
        registry.packetSender(Side.Client, C2SRequestDictionary.TYPE, C2SRequestDictionary.CODEC);

        // Packet handling.
        registry.packetReceiver(C2SRequestKnowledge.TYPE, () -> feature().handlers::handleRequestKnowledge);
        registry.packetReceiver(C2SRequestDictionary.TYPE, () -> feature().handlers::handleRequestDictionary);
    }

    /**
     * Runs when the feature is enabled.
     *
     * @return Runnable.
     */
    @Override
    public Runnable boot() {
        return () -> {
            // Event handler registration.
            ServerLifecycleEvents.SERVER_STARTING.register(feature().handlers::serverStarting);
            ServerLifecycleEvents.SERVER_STARTED.register(feature().handlers::serverStarted);
            ServerEntityEvents.ENTITY_LOAD.register(feature().handlers::entityJoin);
        };
    }
}
