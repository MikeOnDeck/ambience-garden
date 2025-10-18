package charmony.villager_tasks.common.features.villager_tasks.enums;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum TaskType implements StringRepresentable {
    Unspecified(0, "unspecified"),
    Collect(1, "collect"),
    Treasure(2, "treasure"),
    Deliver(3, "deliver"),
    Hunt(4, "hunt"),
    Encounter(5, "encounter"),
    Retrieve(6, "retrieve");

    public static final EnumCodec<TaskType> CODEC = StringRepresentable.fromEnum(TaskType::values);

    private final int id;
    private final String name;

    TaskType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static TaskType fromString(String name) {
        for (var type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return Unspecified;
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
