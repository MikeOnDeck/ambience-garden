package svenhjol.charmony.api.elixirs;

import net.minecraft.core.HolderGetter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;

@SuppressWarnings("unused")
public final class ElixirsApi {
    private static ElixirsApi instance;

    private TriFunction<String, HolderGetter.Provider, RandomSource, ItemStack> elixirImpl;
    private BiFunction<HolderGetter.Provider, RandomSource, ItemStack> randomElixirImpl;

    public static ElixirsApi instance() {
        if (instance == null) {
            instance = new ElixirsApi();
        }
        return instance;
    }

    public ItemStack elixir(String id, HolderGetter.Provider registry, RandomSource random) {
        return elixirImpl.apply(id, registry, random);
    }

    public ItemStack randomElixir(HolderGetter.Provider registry, RandomSource random) {
        return randomElixirImpl.apply(registry, random);
    }

    public static class Impl {
        public static void elixir(TriFunction<String, HolderGetter.Provider, RandomSource, ItemStack> impl) {
            ElixirsApi.instance().elixirImpl = impl;
        }

        public static void randomElixir(BiFunction<HolderGetter.Provider, RandomSource, ItemStack> impl) {
            ElixirsApi.instance().randomElixirImpl = impl;
        }
    }
}
