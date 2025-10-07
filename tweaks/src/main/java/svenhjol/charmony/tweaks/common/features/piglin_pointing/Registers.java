package svenhjol.charmony.tweaks.common.features.piglin_pointing;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.structure.Structure;
import svenhjol.charmony.core.Charmony;
import svenhjol.charmony.core.base.Setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Registers extends Setup<PiglinPointing> {
    public final MemoryModuleType<BlockPos> pointingAtTarget;
    public final List<Pair<TagKey<Item>, TagKey<Structure>>> DIRECTION_BARTERING = new ArrayList<>();

    public Registers(PiglinPointing feature) {
        super(feature);

        var pointingAtTargetId = Charmony.id("pointing_at_target");
        pointingAtTarget = Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE, pointingAtTargetId,
            new MemoryModuleType<>(Optional.empty()));

        registerDirectionBartering(Tags.PIGLIN_BARTERS_FOR_BASTIONS, Tags.PIGLIN_BASTION_LOCATED);
        registerDirectionBartering(Tags.PIGLIN_BARTERS_FOR_FORTRESSES, Tags.PIGLIN_FORTRESS_LOCATED);
    }

    @Override
    public Runnable boot() {
        return () -> {
            // Weaken memory types so we can add our custom one in.
            var memoryTypes = new ArrayList<>(Piglin.MEMORY_TYPES);
            memoryTypes.add(pointingAtTarget);
            Piglin.MEMORY_TYPES = ImmutableList.copyOf(memoryTypes);
        };
    }

    public void registerDirectionBartering(TagKey<Item> items, TagKey<Structure> structure) {
        DIRECTION_BARTERING.add(Pair.of(items, structure));
    }
}
