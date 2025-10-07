package svenhjol.charmony.ambient_sounds.helpers;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public final class BiomeCheckHelper {
    public static final Predicate<Holder<Biome>> BADLANDS =
        holder -> holder.is(BiomeTags.IS_BADLANDS);

    public static final BiPredicate<Holder<Biome>, ResourceKey<Biome>> BEACH =
        (holder, key) -> key.equals(Biomes.BEACH) || key.equals(Biomes.STONY_SHORE);

    public static final Predicate<Holder<Biome>> DESERT =
        holder -> holder.is(ConventionalBiomeTags.IS_DESERT);

    public static final Predicate<Holder<Biome>> FOREST =
        holder -> holder.is(BiomeTags.IS_FOREST) || holder.is(ConventionalBiomeTags.IS_FOREST);

    public static final Predicate<Holder<Biome>> ICY =
        holder -> holder.is(ConventionalBiomeTags.IS_ICY) || holder.is(ConventionalBiomeTags.IS_SNOWY);

    public static final Predicate<Holder<Biome>> JUNGLE =
        holder -> holder.is(BiomeTags.IS_JUNGLE) || holder.is(ConventionalBiomeTags.IS_JUNGLE);

    public static final Predicate<Holder<Biome>> MOUNTAIN = holder -> holder.is(BiomeTags.IS_MOUNTAIN)
        || holder.is(ConventionalBiomeTags.IS_MOUNTAIN)
        || holder.is(ConventionalBiomeTags.IS_WINDSWEPT);

    public static final Predicate<Holder<Biome>> OCEAN =
        holder -> holder.is(BiomeTags.IS_OCEAN) || holder.is(ConventionalBiomeTags.IS_OCEAN);

    public static final Predicate<ResourceKey<Biome>> PALE_GARDEN =
        key -> key.equals(Biomes.PALE_GARDEN);

    public static final BiPredicate<Holder<Biome>, ResourceKey<Biome>> PLAINS =
        (holder, key) -> key.equals(Biomes.PLAINS) || holder.is(ConventionalBiomeTags.IS_PLAINS);

    public static BiPredicate<Holder<Biome>, ResourceKey<Biome>> RIVER =
        (holder, key) -> key.equals(Biomes.RIVER) || holder.is(ConventionalBiomeTags.IS_RIVER);

    public static final Predicate<Holder<Biome>> SAVANNA =
        holder -> holder.is(BiomeTags.IS_SAVANNA);

    public static final Predicate<Holder<Biome>> SWAMP =
        holder -> holder.is(ConventionalBiomeTags.IS_SWAMP);

    public static final Predicate<Holder<Biome>> TAIGA =
        holder -> holder.is(BiomeTags.IS_TAIGA);

    public static final Predicate<Holder<Biome>> THE_END =
        holder -> holder.is(BiomeTags.IS_END);

    public static final BiPredicate<Holder<Biome>, ResourceKey<Biome>> ISOLATED_BIOME =
        (holder, key) -> key.equals(Biomes.PALE_GARDEN) || key.equals(Biomes.DEEP_DARK);
}
