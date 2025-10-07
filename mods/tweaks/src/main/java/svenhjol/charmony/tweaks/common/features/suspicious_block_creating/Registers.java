package svenhjol.charmony.tweaks.common.features.suspicious_block_creating;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.CommonRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Registers extends Setup<SuspiciousBlockCreating> {
    public final Map<Block, Block> blockConversions = new HashMap<>();
    public final Supplier<SoundEvent> createBlockSound;

    public Registers(SuspiciousBlockCreating feature) {
        super(feature);
        var registry = CommonRegistry.forFeature(feature);

        createBlockSound = registry.sound("create_suspicious_block");
        registerBlockConversion(Blocks.SAND, Blocks.SUSPICIOUS_SAND);
        registerBlockConversion(Blocks.GRAVEL, Blocks.SUSPICIOUS_GRAVEL);
    }

    public void registerBlockConversion(Block normal, Block suspicious) {
        blockConversions.put(normal, suspicious);
    }
}
