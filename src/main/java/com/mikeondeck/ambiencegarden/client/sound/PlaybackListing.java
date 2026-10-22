package com.mikeondeck.ambiencegarden.client.sound;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Which sounds the debug overlay is allowed to show, and in what order.
 * Playing means audible right now (volume above zero). A sound that is queued
 * to fade in stays visible even while its volume is still zero.
 */
public final class PlaybackListing {
    public record Entry(String id, float volume, boolean fadingIn) {}

    private PlaybackListing() {}

    public static List<Entry> visibleSorted(List<Entry> entries) {
        var visible = new ArrayList<Entry>();
        for (Entry entry : entries) {
            if (entry.volume() > 0.0f || entry.fadingIn()) {
                visible.add(entry);
            }
        }
        visible.sort(Comparator.comparingDouble(Entry::volume).reversed().thenComparing(Entry::id));
        return List.copyOf(visible);
    }
}
