package charmony.villager_tasks.common.features.villager_tasks.enums;

import charmony.villager_tasks.common.features.villager_tasks.Behavior;
import charmony.villager_tasks.common.features.villager_tasks.behaviors.Collect;
import charmony.villager_tasks.common.features.villager_tasks.behaviors.None;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum TaskType implements StringRepresentable {
    Unspecified(0, "unspecified", None.class),
    Collect(1, "collect", Collect.class),
    Treasure(2, "treasure", Collect.class),
    Deliver(3, "deliver", Collect.class),
    Hunt(4, "hunt", Collect.class),
    Encounter(5, "encounter", Collect.class),
    Retrieve(6, "retrieve", Collect.class);

    public static final EnumCodec<TaskType> CODEC = StringRepresentable.fromEnum(TaskType::values);

    private final int id;
    private final String name;
    private final Class<?> behavior;

    TaskType(int id, String name, Class<?> behavior) {
        this.id = id;
        this.name = name;
        this.behavior = behavior;
    }

    public static TaskType fromString(String name) {
        for (var type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new RuntimeException("Unknown TaskType name: " + name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Behavior getBehavior() {
        Behavior instance;

        try {
            instance = (Behavior) behavior.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not load behavior for TaskType: " + name);
        }

        return instance;
    }

    @Override
    public String getSerializedName() {
        return name.toLowerCase(Locale.ROOT);
    }
}
