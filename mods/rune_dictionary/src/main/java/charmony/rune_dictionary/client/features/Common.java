package charmony.rune_dictionary.client.features;

import charmony.rune_dictionary.common.features.rune_dictionary.Handlers;
import charmony.rune_dictionary.common.features.rune_dictionary.RuneDictionary;

public class Common {
    public final RuneDictionary feature;
    public final Handlers handlers;

    public Common() {
        feature = RuneDictionary.feature();
        handlers = feature.handlers;
    }
}
