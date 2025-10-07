package svenhjol.charmony.api.stone_chests;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;

import java.util.Locale;
import java.util.function.IntFunction;

/**
 * Materials for stone chests.
 */
@SuppressWarnings({"NullableProblems", "unused"})
public enum StoneChestMaterial implements StringRepresentable {
    Stone(0, "stone", DyeColor.LIGHT_GRAY),
    Deepslate(1, "deepslate", DyeColor.GRAY),
    Prismarine(2, "prismarine", DyeColor.CYAN),
    Blackstone(3, "blackstone", DyeColor.BLACK),
    NetherBrick(4, "nether_brick", DyeColor.BROWN),
    Purpur(5, "purpur", DyeColor.PURPLE),
    Calcite(6, "calcite", DyeColor.WHITE),
    Tuff(7, "tuff", DyeColor.BLACK);

    public static final EnumCodec<StoneChestMaterial> CODEC = StringRepresentable.fromEnum(StoneChestMaterial::values);
    public static final IntFunction<StoneChestMaterial> BY_ID = ByIdMap.continuous(StoneChestMaterial::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    private final int id;
    private final String name;
    private final DyeColor color;

    StoneChestMaterial(int id, String name, DyeColor color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static StoneChestMaterial byId(int id) {
        return BY_ID.apply(id);
    }

    public int getId() {
        return id;
    }

    public DyeColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getSerializedName() {
        return name.toLowerCase(Locale.ROOT);
    }
}
