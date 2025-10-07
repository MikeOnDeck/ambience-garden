package svenhjol.charmony.api.relics;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum RelicType implements StringRepresentable {
    Unknown(0, "unknown"),
    Artifact(1, "artifact"),
    Book(2, "book"),
    Weapon(3, "weapon"),
    Tool(4, "tool"),
    Armor(5, "armor");

    private static final IntFunction<RelicType> BY_ID = ByIdMap.continuous(RelicType::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    public static final EnumCodec<RelicType> CODEC = StringRepresentable.fromEnum(RelicType::values);

    public static final StreamCodec<ByteBuf, RelicType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, RelicType::getId);

    private final int id;

    private final String name;

    RelicType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
