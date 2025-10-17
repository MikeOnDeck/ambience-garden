package charmony.villager_tasks;

import charmony.api.core.ModDefinition;
import charmony.api.core.Side;
import charmony.core.base.Mod;
import net.minecraft.resources.ResourceLocation;

@ModDefinition(id = VillagerTasksMod.ID, sides = {Side.Common, Side.Client},
    name = "Villager Tasks",
    description = "Villagers give tasks that can be completed for rewards.")
public class VillagerTasksMod extends Mod {
    public static final String ID = "charmony-villager-tasks";

    private static VillagerTasksMod instance;

    public static VillagerTasksMod instance() {
        if (instance == null) {
            instance = new VillagerTasksMod();
        }
        return instance;
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}
