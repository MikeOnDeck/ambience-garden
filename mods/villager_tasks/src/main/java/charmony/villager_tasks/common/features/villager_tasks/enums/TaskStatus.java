package charmony.villager_tasks.common.features.villager_tasks.enums;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum TaskStatus implements StringRepresentable {
    Unspecified(0, "unspecified"),
    NotStarted(1, "not_started"),
    Starting(2, "starting"),
    InProgress(3, "in_progress"),
    Satisfied(4, "satisfied"),
    Completed(5, "completed"),
    Abandoned(6, "abandoned");

    public static final EnumCodec<TaskStatus> CODEC = StringRepresentable.fromEnum(TaskStatus::values);

    private final int id;
    private final String name;

    TaskStatus(int id, String name) {
        this.id = id;
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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getSerializedName() {
        return name.toLowerCase(Locale.ROOT);
    }
}
