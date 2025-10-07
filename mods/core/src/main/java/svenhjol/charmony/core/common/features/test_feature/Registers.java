package svenhjol.charmony.core.common.features.test_feature;

import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Items;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.CommonRegistry;

public class Registers extends Setup<TestFeature> {
    public Registers(TestFeature feature) {
        super(feature);

        var registry = CommonRegistry.forFeature(feature);
        registry.wandererTrade(() -> new VillagerTrades.EmeraldForItems(Items.LAVA_BUCKET, 1, 1, 1, 2), true);
    }
}
