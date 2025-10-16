package charmony.api.chorus_network;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Locale;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Core materials for chorus network cores and chests.
 * In future we may want custom colors here. For now just register the closest DyeColor.
 */
@SuppressWarnings({"NullableProblems", "unused"})
public enum ChorusCoreMaterial implements StringRepresentable {
    Unspecified(0, "unspecified", () -> Items.AIR, DyeColor.WHITE),
    Quartz(1, "quartz", () -> Items.QUARTZ, DyeColor.WHITE),
    Iron(2, "iron", () -> Items.IRON_INGOT, DyeColor.GRAY),
    Netherite(3, "netherite", () -> Items.NETHERITE_INGOT, DyeColor.BLACK),
    Copper(4, "copper", () -> Items.COPPER_INGOT, DyeColor.BROWN),
    Redstone(5, "redstone", () -> Items.REDSTONE, DyeColor.RED),
    Resin(6, "resin", () -> Items.RESIN_BRICK, DyeColor.ORANGE),
    Gold(7, "gold", () -> Items.GOLD_INGOT, DyeColor.YELLOW),
    Emerald(8, "emerald", () -> Items.EMERALD, DyeColor.GREEN),
    Diamond(9, "diamond", () -> Items.DIAMOND, DyeColor.CYAN),
    Lapis(10, "lapis", () -> Items.LAPIS_LAZULI, DyeColor.BLUE),
    Amethyst(11, "amethyst", () -> Items.AMETHYST_SHARD, DyeColor.PURPLE);

    public static final EnumCodec<ChorusCoreMaterial> CODEC = StringRepresentable.fromEnum(ChorusCoreMaterial::values);
    public static final IntFunction<ChorusCoreMaterial> BY_ID = ByIdMap.continuous(ChorusCoreMaterial::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);

    private final int id;
    private final String name;
    private final Supplier<Item> item;
    private final DyeColor color;

    ChorusCoreMaterial(int id, String name, Supplier<Item> item, DyeColor color) {
        this.id = id;
        this.name = name;
        this.item = item;
        this.color = color;
    }

    public static ChorusCoreMaterial byId(int id) {
        return BY_ID.apply(id);
    }

    public static Optional<ChorusCoreMaterial> byName(String name) {
        for (var material : values()) {
            if (material.name.equals(name)) {
                return Optional.of(material);
            }
        }
        return Optional.empty();
    }

    public static Optional<ChorusCoreMaterial> byItem(ItemStack item) {
        for (var material : values()) {
            if (material.item.get().equals(item.getItem())) {
                return Optional.of(material);
            }
        }
        return Optional.empty();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Supplier<Item> getItem() {
        return item;
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    public String getSerializedName() {
        return name.toLowerCase(Locale.ROOT);
    }
}
