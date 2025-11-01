package charmony.villager_tasks.common.features.villager_tasks.requirements;

import charmony.villager_tasks.common.features.villager_tasks.interfaces.HasWeight;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class HuntMob implements Satisfiable, HasWeight {
    private final ResourceLocation mob;
    private final int total;
    private final int weight;
    private int hunted;

    public static final Codec<HuntMob> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("mob").forGetter(self -> self.mob),
        Codec.INT.fieldOf("total").forGetter(self -> self.total),
        Codec.INT.fieldOf("hunted").forGetter(self -> self.hunted),
        Codec.INT.fieldOf("weight").forGetter(self -> self.weight)
    ).apply(instance, HuntMob::new));

    public HuntMob(ResourceLocation mob, int total, int hunted, int weight) {
        this.mob = mob;
        this.total = total;
        this.hunted = hunted;
        this.weight = weight;
    }

    @Override
    public int weight() {
        return weight;
    }

    @Override
    public boolean isSatisfied() {
        return remaining() == 0;
    }

    @Override
    public int remaining() {
        return Math.max(0, total - hunted);
    }

    @Override
    public int total() {
        return total;
    }

    public ResourceLocation mob() {
        return mob;
    }

    public ResourceKey<EntityType<?>> mobKey() {
        return ResourceKey.create(Registries.ENTITY_TYPE, mob);
    }

    public int getHunted() {
        return hunted;
    }

    public void addHunted() {
        hunted += 1;
    }
}
