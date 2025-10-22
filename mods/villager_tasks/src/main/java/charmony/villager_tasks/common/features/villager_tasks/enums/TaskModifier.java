package charmony.villager_tasks.common.features.villager_tasks.enums;

import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum TaskModifier implements StringRepresentable {
    Unspecified(0, "unspecified", 1.0, 1.0, false),
    Angry(1, "angry", 0.5, 2.0, false),
    Unhapy(2, "unhappy", 0.75, 1.5, false),
    Normal(3, "normal", 1.0, 1.0, false),
    Happy(4, "happy", 1.5, 1.0, false),
    Epic(5, "epic",3.0, 1.0, true);

    public static final EnumCodec<TaskModifier> CODEC = StringRepresentable.fromEnum(TaskModifier::values);

    private final int id;
    private final String name;
    private final double positiveMultiplier;
    private final double negativeMultiplier;
    private final boolean epic;

    TaskModifier(int id, String name, double positive, double negative, boolean epic) {
        this.id = id;
        this.name = name;
        this.positiveMultiplier = positive;
        this.negativeMultiplier = negative;
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

    public double getPositiveMultiplier(RandomSource random) {
        return Math.max(0.1, positiveMultiplier + (random.nextDouble() * 0.5d));
    }

    public double getNegativeMultiplier(RandomSource random) {
        return Math.max(0.1, negativeMultiplier + (random.nextDouble() * 0.5d));
    }

    public boolean isEpic() {
        return epic;
    }

    @Override
    public String getSerializedName() {
        return name.toLowerCase(Locale.ROOT);
    }
}
