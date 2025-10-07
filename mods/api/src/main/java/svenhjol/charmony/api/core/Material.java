package svenhjol.charmony.api.core;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

@SuppressWarnings("unused")
public interface Material extends StringRepresentable {
    default boolean isFlammable() {
        return false;
    }

    default MapColor mapColor() {
        return MapColor.COLOR_GRAY;
    }

    default int fuelTime() {
        return isFlammable() ? 300 : 0;
    }

    default int igniteChance() {
        return isFlammable() ? 30 : 0;
    }

    default int burnChance() {
        return isFlammable() ? 20 : 0;
    }

    default NoteBlockInstrument noteBlockInstrument() {
        return NoteBlockInstrument.HARP; // Vanilla default
    }

    SoundType soundType();

    default BlockBehaviour.Properties blockProperties() {
        return BlockBehaviour.Properties.of()
            .sound(soundType())
            .instrument(noteBlockInstrument())
            .mapColor(mapColor());
    }
}
