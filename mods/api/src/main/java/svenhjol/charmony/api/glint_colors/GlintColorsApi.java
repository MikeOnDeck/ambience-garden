package svenhjol.charmony.api.glint_colors;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class GlintColorsApi {
    private static GlintColorsApi instance;

    private BiConsumer<ItemStack, DyeColor> applyImpl;
    private Function<ItemStack, Boolean> hasImpl;
    private Consumer<ItemStack> removeImpl;
    private Function<ItemStack, Optional<DyeColor>> getColorImpl;

    private GlintColorsApi() {
        applyImpl = (stack, color) -> {};
        hasImpl = stack -> false;
        removeImpl = stack -> {};
        getColorImpl = stack -> Optional.empty();
    }

    public static GlintColorsApi instance() {
        if (instance == null) {
            instance = new GlintColorsApi();
        }
        return instance;
    }

    public void apply(ItemStack stack, DyeColor color) {
        applyImpl.accept(stack, color);
    }

    public boolean has(ItemStack stack) {
        return hasImpl.apply(stack);
    }

    public void remove(ItemStack stack) {
        removeImpl.accept(stack);
    }

    public Optional<DyeColor> getColor(ItemStack stack) {
        return getColorImpl.apply(stack);
    }

    public static class Impl {
        public static void apply(BiConsumer<ItemStack, DyeColor> impl) {
            GlintColorsApi.instance().applyImpl = impl;
        }

        public static void getColor(Function<ItemStack, Optional<DyeColor>> impl) {
            GlintColorsApi.instance().getColorImpl = impl;
        }

        public static void has(Function<ItemStack, Boolean> impl) {
            GlintColorsApi.instance().hasImpl = impl;
        }

        public static void remove(Consumer<ItemStack> impl) {
            GlintColorsApi.instance().removeImpl = impl;
        }
    }
}
