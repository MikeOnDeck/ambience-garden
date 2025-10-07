package svenhjol.charmony.api.relics;

import net.minecraft.core.HolderGetter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;

@SuppressWarnings("unused")
public final class RelicsApi {
    private static RelicsApi instance;

    private TriFunction<String, HolderGetter.Provider, RandomSource, ItemStack> relicImpl;
    private BiFunction<HolderGetter.Provider, RandomSource, ItemStack> randomRelicImpl;
    private TriFunction<HolderGetter.Provider, RandomSource, RelicType, ItemStack> randomRelicOfTypeImpl;

    public static RelicsApi instance() {
        if (instance == null) {
            instance = new RelicsApi();
        }
        return instance;
    }

    private RelicsApi() {
        // Set empty implementations on first init. These implementations will be populated by the relics mod, if present.
        relicImpl = (id, registry, random) -> ItemStack.EMPTY;
        randomRelicImpl = (registry, random) -> ItemStack.EMPTY;
        randomRelicOfTypeImpl = (registry, random, type) -> ItemStack.EMPTY;
    }

    public ItemStack relic(String id, HolderGetter.Provider registry, RandomSource random) {
        return relicImpl.apply(id, registry, random);
    }

    public ItemStack randomRelic(HolderGetter.Provider registry, RandomSource random) {
        return randomRelicImpl.apply(registry, random);
    }

    public ItemStack randomRelicOfType(HolderGetter.Provider registry, RandomSource random, RelicType type) {
        return randomRelicOfTypeImpl.apply(registry, random, type);
    }

    public static class Impl {
        public static void relic(TriFunction<String, HolderGetter.Provider, RandomSource, ItemStack> impl) {
            RelicsApi.instance().relicImpl = impl;
        }

        public static void randomRelic(BiFunction<HolderGetter.Provider, RandomSource, ItemStack> impl) {
            RelicsApi.instance().randomRelicImpl = impl;
        }

        public static void randomRelicOfType(TriFunction<HolderGetter.Provider, RandomSource, RelicType, ItemStack> impl) {
            RelicsApi.instance().randomRelicOfTypeImpl = impl;
        }
    }
}
