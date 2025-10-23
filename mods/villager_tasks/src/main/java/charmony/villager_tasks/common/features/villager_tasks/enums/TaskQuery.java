package charmony.villager_tasks.common.features.villager_tasks.enums;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum TaskQuery implements StringRepresentable {
    Unspecified("unspecified"),
    Abandon("abandon"),
    Accept("accept"),
    Complete("complete");

    public static final EnumCodec<TaskQuery> CODEC = StringRepresentable.fromEnum(TaskQuery::values);

    private final String name;

    TaskQuery(String name) {
        this.name = name;
    }

    public static TaskQuery fromString(String name) {
        for (var status : values()) {
            if (status.name.equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new RuntimeException("Unknown TaskQuery name: " + name);
    }

    @Override
    public String getSerializedName() {
        return name.toLowerCase(Locale.ROOT);
    }
}
