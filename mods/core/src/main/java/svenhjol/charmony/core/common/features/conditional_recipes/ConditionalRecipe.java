package svenhjol.charmony.core.common.features.conditional_recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Predicate;

public class ConditionalRecipe {
    protected final ResourceLocation id;
    protected final Predicate<ConditionalRecipe> predicate;

    protected String type;
    protected String category;
    protected String result;
    protected int count = 1;
    protected List<String> pattern = new ArrayList<>();
    protected Map<String, String> key;

    public ConditionalRecipe(ResourceLocation id, Predicate<ConditionalRecipe> predicate) {
        this.id = id;
        this.predicate = predicate;
        this.useShapedCrafting();
    }

    public ConditionalRecipe withType(String type) {
        this.type = type;
        return this;
    }

    public ConditionalRecipe useShapedCrafting() {
        this.type = "minecraft:crafting_shaped";
        return this;
    }

    public ConditionalRecipe withCategory(String category) {
        this.category = category;
        return this;
    }

    public ConditionalRecipe withPattern(String ...rows) {
        pattern.clear();
        pattern.addAll(Arrays.asList(rows));
        return this;
    }

    public ConditionalRecipe withKey(Map<String, String> map) {
        this.key = map;
        return this;
    }

    public ConditionalRecipe withResult(String result) {
        this.result = result;
        return this;
    }

    public ConditionalRecipe withCount(int count) {
        this.count = count;
        return this;
    }

    public ResourceLocation id() {
        return this.id;
    }

    public Optional<JsonObject> create() {
        if (!predicate
            .and(x -> x.count > 0)
            .and(x -> x.result != null)
            .and(x -> !x.pattern.isEmpty())
            .test(this)) {
            return Optional.empty();
        }

        var out = new JsonObject();
        out.addProperty("type", this.type);

        if (this.category != null) {
            out.addProperty("category", this.category);
        }

        var pattern = new JsonArray();
        for (var row : this.pattern) {
            pattern.add(row);
        }
        out.add("pattern", pattern);

        var key = new JsonObject();
        for (var entry : this.key.entrySet()) {
            var k = entry.getKey();
            var v = entry.getValue();
            key.addProperty(k, v);
        }
        out.add("key", key);

        var result = new JsonObject();
        result.addProperty("id", this.result);
        result.addProperty("count", this.count);
        out.add("result", result);

        return Optional.of(out);
    }
}
