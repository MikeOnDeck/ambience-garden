package charmony.villager_tasks.common.features.villager_tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record Loyalty(UUID uuid, String name, List<VillagerLoyalty> loyalties) {
    public static final Codec<Loyalty> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        UUIDUtil.CODEC.fieldOf("uuid").forGetter(Loyalty::uuid),
        Codec.STRING.fieldOf("name").forGetter(Loyalty::name),
        VillagerLoyalty.CODEC.listOf().fieldOf("loyalties").forGetter(Loyalty::loyalties)
    ).apply(instance, Loyalty::new));

    /**
     * Increase the loyalty for a villager and return a new Loyalty record.
     *
     * @param villager Villager UUID.
     * @return New loyalty record.
     */
    public Loyalty addLoyalty(UUID villager) {
        var updated = new ArrayList<>(loyalties());
        var entry = updated.stream()
            .filter(vl -> vl.villager().equals(villager))
            .findFirst();

        // Get existing points by looking for an existing loyalty entry.
        var currentPoints = entry.map(VillagerLoyalty::loyalty).orElse(0);
        updated.removeIf(vl -> vl.villager().equals(villager));

        updated.add(new VillagerLoyalty(villager, currentPoints + 1));
        return new Loyalty(uuid, name, updated);
    }

    /**
     * Remove any existing loyalty entries for the villager.
     *
     * @param villager Villager UUID.
     * @return New loyalty record.
     */
    public Loyalty resetLoyalty(UUID villager) {
        var updated = new ArrayList<>(loyalties());
        updated.removeIf(vl -> vl.villager().equals(villager));
        return new Loyalty(uuid, name, updated);
    }

    /**
     * Get the loyalty points for a villager, defaulting to zero.
     *
     * @param villager Villager UUID.
     * @return Loyalty points.
     */
    public int getLoyalty(UUID villager) {
        return loyalties().stream()
            .filter(vl -> vl.villager().equals(villager))
            .map(VillagerLoyalty::loyalty)
            .findFirst()
            .orElse(0);
    }

    public record VillagerLoyalty(UUID villager, int loyalty) {
        public static final Codec<VillagerLoyalty> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("villager").forGetter(VillagerLoyalty::villager),
            Codec.INT.fieldOf("loyalty").forGetter(VillagerLoyalty::loyalty)
        ).apply(instance, VillagerLoyalty::new));
    }
}
