package charmony.rune_dictionary.client.features;

import charmony.core.base.Setup;
import charmony.core.client.ClientRegistry;
import charmony.rune_dictionary.common.features.rune_dictionary.Networking;

public class Registers extends Setup<RuneDictionary> {
    public Registers(RuneDictionary feature) {
        super(feature);

        var registry = ClientRegistry.forFeature(feature);

        registry.packetReceiver(Networking.S2CDictionary.TYPE,
            feature.handlers::handleDictionary);
        registry.packetReceiver(Networking.S2CKnowledge.TYPE,
            feature.handlers::handleKnowledge);
    }
}
