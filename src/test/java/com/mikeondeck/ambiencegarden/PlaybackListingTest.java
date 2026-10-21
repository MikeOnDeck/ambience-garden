package com.mikeondeck.ambiencegarden;

import com.mikeondeck.ambiencegarden.client.sound.AmbientSoundIds;
import com.mikeondeck.ambiencegarden.client.sound.PlaybackListing;
import com.mikeondeck.ambiencegarden.client.sound.SoundCatalog;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaybackListingTest {
    @Test
    void overlayKeepsAudibleSoundsAndFadeInsLoudestFirst() {
        var rows = List.of(
            new PlaybackListing.Entry("silent", 0.0f, false),
            new PlaybackListing.Entry("quiet", 0.05f, false),
            new PlaybackListing.Entry("fading", 0.0f, true),
            new PlaybackListing.Entry("loud", 0.80f, false),
            new PlaybackListing.Entry("mid", 0.40f, true)
        );

        var visible = PlaybackListing.visibleSorted(rows);

        assertEquals(List.of("loud", "mid", "quiet", "fading"), visible.stream().map(PlaybackListing.Entry::id).toList());
        assertTrue(visible.get(1).fadingIn());
        assertTrue(visible.get(3).fadingIn());
        assertEquals(0.0f, visible.get(3).volume());
        assertFalse(visible.stream().anyMatch(entry -> entry.id().equals("silent")));
    }

    @Test
    void catalogListsEverySoundOnceGroupedByBank() {
        var paths = SoundCatalog.all().stream().map(SoundCatalog.Entry::path).toList();
        Set<String> unique = new LinkedHashSet<>(paths);
        assertEquals(new LinkedHashSet<>(AmbientSoundIds.PATHS), unique);
        assertEquals(unique.size(), paths.size());

        var banks = SoundCatalog.banks();
        assertTrue(banks.stream().anyMatch(bank -> bank.group() == SoundCatalog.Group.BIOME && bank.name().equals("Desert")));
        assertTrue(banks.stream().anyMatch(bank -> bank.group() == SoundCatalog.Group.ENVIRONMENT && bank.name().equals("Villages")));
        assertTrue(banks.stream().allMatch(bank -> !bank.entries().isEmpty()));
    }
}
