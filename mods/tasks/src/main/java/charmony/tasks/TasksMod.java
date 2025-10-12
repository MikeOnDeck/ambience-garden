package charmony.tasks;

import charmony.api.core.ModDefinition;
import charmony.api.core.Side;
import charmony.core.base.Mod;

@ModDefinition(id = TasksMod.ID, sides = {Side.Common, Side.Client},
    name = "Tasks",
    description = "Villagers give tasks that can be completed for rewards.")
public class TasksMod extends Mod {
    public static final String ID = "charmony-tasks";
    private static TasksMod instance;

    public static TasksMod instance() {
        if (instance == null) {
            instance = new TasksMod();
        }
        return instance;
    }
}
