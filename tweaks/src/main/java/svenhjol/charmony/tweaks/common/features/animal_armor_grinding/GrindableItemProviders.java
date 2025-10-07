package svenhjol.charmony.tweaks.common.features.animal_armor_grinding;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import svenhjol.charmony.api.tweaks.GrindableItemProvider;
import svenhjol.charmony.core.Api;
import svenhjol.charmony.core.base.Setup;

import java.util.List;

public class GrindableItemProviders extends Setup<AnimalArmorGrinding> implements GrindableItemProvider {
    public GrindableItemProviders(AnimalArmorGrinding feature) {
        super(feature);
        Api.registerProvider(this);
    }

    @Override
    public List<Pair<ItemLike, ItemLike>> getItemGrindResults() {
        return List.of(
            Pair.of(Items.WOLF_ARMOR, Items.ARMADILLO_SCUTE),
            Pair.of(Items.SADDLE, Items.LEATHER),
            Pair.of(Items.LEATHER_HORSE_ARMOR, Items.LEATHER),
            Pair.of(Items.IRON_HORSE_ARMOR, Items.IRON_INGOT),
            Pair.of(Items.GOLDEN_HORSE_ARMOR, Items.GOLD_INGOT),
            Pair.of(Items.DIAMOND_HORSE_ARMOR, Items.DIAMOND)
        );
    }
}
