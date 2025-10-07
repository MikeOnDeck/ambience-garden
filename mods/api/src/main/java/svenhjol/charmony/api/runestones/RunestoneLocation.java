package svenhjol.charmony.api.runestones;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public record RunestoneLocation(Type type, ResourceLocation id) {
    public static final ResourceLocation UNKNOWN = ResourceLocation.withDefaultNamespace("unknown");
    public static final String TYPE_TAG = "type";
    public static final String ID_TAG = "id";

    public boolean is(RunestoneLocation location) {
        return type.equals(location.type()) && id.equals(location.id());
    }

    public CompoundTag save() {
        var tag = new CompoundTag();
        tag.putString(TYPE_TAG, type().name());
        tag.putString(ID_TAG, id().toString());
        return tag;
    }

    public static RunestoneLocation load(CompoundTag tag) {
        var type = tag.getString(TYPE_TAG).map(Type::valueOf).orElse(null);
        var id = tag.getString(ID_TAG).map(ResourceLocation::parse).orElse(null);

        if (type == null || id == null) {
            return new RunestoneLocation(Type.Structure, UNKNOWN);
        }

        return new RunestoneLocation(type, id);
    }

    public enum Type {
        Biome,
        Player,
        Structure
    }
}
