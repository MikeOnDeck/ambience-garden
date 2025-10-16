package charmony.villager_tasks.common.features.villager_tasks;

import com.google.gson.Gson;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"FieldMayBeFinal", "unused"})
public class Definition {
    private ResourceLocation id;
    private ResourceManager manager; // TODO: check why this is needed

    // These are loaded from the JSON definition.
    public List<String> types = new ArrayList<>();
    public int level = 0;
    public int expiry = 0;
    public double multiplier = 1.0d;
    public String villager = "";
    public Map<String, Object> collect = new HashMap<>();
    public Map<String, Object> deliver = new HashMap<>();
    public Map<String, Object> treasure = new HashMap<>();
    public Map<String, Object> hunt = new HashMap<>();
    public Map<String, Object> encounter = new HashMap<>();
    public Map<String, Object> retrieve = new HashMap<>();
    public Map<String, Object> rewards = new HashMap<>();
    public Map<String, Object> penalties = new HashMap<>();
    public Map<String, Object> effects = new HashMap<>();

    public static Definition fromJson(ResourceLocation id, ResourceManager manager, Resource resource) throws IOException {
        BufferedReader reader;

        reader = resource.openAsReader();
        var def = new Gson().fromJson(reader, Definition.class);

        def.id = id;
        def.manager = manager;
        return def;
    }
}
