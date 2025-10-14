package charmony.villager_tasks.common.features.villager_tasks;

import com.google.gson.Gson;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.io.IOException;

public class Definition {
    private ResourceLocation id;
    private ResourceManager manager; // TODO: check why this is needed

    public static Definition fromJson(ResourceLocation id, ResourceManager manager, Resource resource) throws IOException {
        BufferedReader reader;

        reader = resource.openAsReader();
        var def = new Gson().fromJson(reader, Definition.class);

        def.id = id;
        def.manager = manager;
        return def;
    }
}
