package svenhjol.charmony.core.common.features.wood.types;

import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import svenhjol.charmony.core.common.features.wood.CustomWood;
import svenhjol.charmony.core.common.features.wood.CustomWoodType;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;
import svenhjol.charmony.core.common.features.wood.WoodRegistry;

import java.util.function.Supplier;

public class Boat extends CustomWoodType {
    public Supplier<EntityType<net.minecraft.world.entity.vehicle.Boat>> boat;
    public Supplier<EntityType<net.minecraft.world.entity.vehicle.ChestBoat>> chestBoat;
    public final Supplier<Item> boatItem;
    public final Supplier<Item> chestBoatItem;

    public Boat(WoodRegistry woodRegistry, WoodMaterial material) {
        super(woodRegistry, material);

        var commonRegistry = woodRegistry.commonRegistry();
        var materialName = material.getSerializedName();

        boatItem = commonRegistry.item(materialName + "_boat",
            key -> new BoatItem(boat.get(), new Item.Properties()
                .stacksTo(1)
                .setId(key)));

        chestBoatItem = commonRegistry.item(materialName + "_chest_boat",
            key -> new BoatItem(chestBoat.get(), new Item.Properties()
                .stacksTo(1)
                .setId(key)));

        boat = woodRegistry.boatEntity(material, boatItem);
        chestBoat = woodRegistry.chestBoatEntity(material, chestBoatItem);

        // Add default boat and chest boat dispenser behavior.
        commonRegistry.dispenserBehavior(boatItem, () -> new BoatDispenseItemBehavior(boat.get()));
        commonRegistry.dispenserBehavior(chestBoatItem, () -> new BoatDispenseItemBehavior(chestBoat.get()));

        // Add to creative menu.
        woodRegistry.addItemToCreativeTab(boatItem, material, CustomWood.BOAT);
        woodRegistry.addItemToCreativeTab(chestBoatItem, material, CustomWood.CHEST_BOAT);
    }
}
