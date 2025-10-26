package charmony.villager_tasks.common.features.villager_tasks.enums;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum TaskStatus implements StringRepresentable {
    Unspecified("unspecified"),
    NotStarted("not_started"),
    Starting("starting"),
    InProgress("in_progress");

    public static final EnumCodec<TaskStatus> CODEC = StringRepresentable.fromEnum(TaskStatus::values);

    private final String name;

    TaskStatus(String name) {
        this.name = name;
    }

    public static TaskStatus fromString(String name) {
        for (var status : values()) {
            if (status.name.equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new RuntimeException("Unknown TaskStatus name: " + name);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getSerializedName() {
        return name.toLowerCase(Locale.ROOT);
    }
}
