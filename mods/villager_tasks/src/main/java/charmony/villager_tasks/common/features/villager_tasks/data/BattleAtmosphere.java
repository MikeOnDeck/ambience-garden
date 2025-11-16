package charmony.villager_tasks.common.features.villager_tasks.data;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.Optional;

public enum BattleAtmosphere implements StringRepresentable {
    Storm("storm");

    public static final EnumCodec<BattleAtmosphere> CODEC = StringRepresentable.fromEnum(BattleAtmosphere::values);

    private final String name;

    BattleAtmosphere(String name) {
        this.name = name;
    }

    public static Optional<BattleAtmosphere> fromString(String str) {
        for (var atmosphere : BattleAtmosphere.values()) {
            if (atmosphere.getSerializedName().equalsIgnoreCase(str)) {
                return Optional.of(atmosphere);
            }
        }
        return Optional.empty();
    }

    @Override
    public String getSerializedName() {
        return name.toLowerCase(Locale.ROOT);
    }
}
