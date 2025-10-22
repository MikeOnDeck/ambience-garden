package charmony.villager_tasks.common.features.villager_tasks.enums;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum TaskModifier implements StringRepresentable {
    Unspecified("unspecified", -1024, 1.0, 1.0, false),
    Hated("hated", -100, 0.15, 2.5, false),
    Angry("angry", -50, 0.4, 2.0, false),
    Unhappy( "unhappy", -25, 0.75, 1.5, false),
    Normal( "normal", 18, 1.0, 1.0, false),
    Happy( "happy", 25, 1.2, 0.9, false),
    Epic( "epic",1024, 3.0, 1.5, true);

    public static final EnumCodec<TaskModifier> CODEC = StringRepresentable.fromEnum(TaskModifier::values);

    private final String name;
    private final int reputation;
    private final double positive;
    private final double negative;
    private final boolean epic;

    TaskModifier(String name, int reputation, double positive, double negative, boolean epic) {
        this.name = name;
        this.reputation = reputation;
        this.positive = positive;
        this.negative = negative;
        this.epic = epic;
    }

    public static TaskModifier fromString(String name) {
        for (var status : values()) {
            if (status.name.equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new RuntimeException("Unknown TaskModifier name: " + name);
    }

    public double positiveMultiplier() {
        return Math.max(0.1, positive);
    }

    public double negativeMultiplier() {
        return Math.max(0.1, negative);
    }

    public boolean isEpic() {
        return epic;
    }

    public int reputation() {
        return reputation;
    }

    public static TaskModifier fromReputation(int reputation) {
        for (var value : values()) {
            if (reputation <= value.reputation) {
                return value;
            }
        }
        return Normal;
    }

    @Override
    public String getSerializedName() {
        return name.toLowerCase(Locale.ROOT);
    }
}
