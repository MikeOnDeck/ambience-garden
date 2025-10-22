package charmony.villager_tasks.common.features.villager_tasks.enums;

import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum TaskModifier implements StringRepresentable {
    Unspecified(0, "unspecified", 0.0, 1.0, 1.0, false),
    Angry(1, "angry", 0.2, 0.5, 2.0, false),
    Unhapy(2, "unhappy", 0.6, 0.75, 1.5, false),
    Normal(3, "normal", 1.0, 1.0, 1.0, false),
    Happy(4, "happy", 1.5, 1.5, 1.0, false),
    Epic(5, "epic", 2.0, 3.0, 1.0, true);

    public static final EnumCodec<TaskModifier> CODEC = StringRepresentable.fromEnum(TaskModifier::values);

    private final int id;
    private final String name;
    private final double opinion;
    private final double positiveMultiplier;
    private final double negativeMultiplier;
    private final boolean epic;

    TaskModifier(int id, String name, double opinion, double positive, double negative, boolean epic) {
        this.id = id;
        this.name = name;
        this.opinion = opinion;
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

    public double getPositiveMultiplier(double min, RandomSource random) {
        var max = Math.max(min, positiveMultiplier);
        return Math.max(min, random.nextDouble() * max);
    }

    public double getNegativeMultiplier(double min, RandomSource random) {
        var max = Math.max(min, negativeMultiplier);
        return Math.max(min, random.nextDouble() * max);
    }

    public double getOpinion() {
        return opinion;
    }

    public boolean isEpic() {
        return epic;
    }

    @Override
    public String getSerializedName() {
        return name.toLowerCase(Locale.ROOT);
    }
}
