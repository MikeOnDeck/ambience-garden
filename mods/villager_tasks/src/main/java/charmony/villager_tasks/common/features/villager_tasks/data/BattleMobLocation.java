package charmony.villager_tasks.common.features.villager_tasks.data;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum BattleMobLocation implements StringRepresentable {
    Unspecified("unspecified"),
    Structure("structure"),
    Biome("biome");

    public static final EnumCodec<BattleMobLocation> CODEC = StringRepresentable.fromEnum(BattleMobLocation::values);

    private final String name;

    BattleMobLocation(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name.toLowerCase(Locale.ROOT);
    }
}
