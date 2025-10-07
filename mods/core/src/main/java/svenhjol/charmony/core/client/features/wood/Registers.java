package svenhjol.charmony.core.client.features.wood;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.client.ClientRegistry;
import svenhjol.charmony.core.common.features.wood.CustomWood;
import svenhjol.charmony.core.common.features.wood.WoodRegistry;

import java.util.ArrayList;
import java.util.List;

public class Registers extends Setup<Wood> {
    public Registers(Wood feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        var clientRegistry = ClientRegistry.forFeature(feature());

        return () -> {
            // Register models for custom boats.
            for (var boat : WoodRegistry.BOATS) {
                var materialName = boat.get().material().getSerializedName();
                var feature = boat.get().feature();

                var boatEntity = boat.get().boat.get();
                var chestBoatEntity = boat.get().chestBoat.get();

                var boatLayer = new ModelLayerLocation(feature.registryId("boat/" + materialName), "main");
                var chestBoatLayer = new ModelLayerLocation(feature.registryId("chest_boat/" + materialName), "main");

                clientRegistry.modelLayer(boatLayer, BoatModel::createBoatModel);
                clientRegistry.modelLayer(chestBoatLayer, BoatModel::createChestBoatModel);

                clientRegistry.entityRenderer(boatEntity, context -> new BoatRenderer(context, boatLayer));
                clientRegistry.entityRenderer(chestBoatEntity, context -> new BoatRenderer(context, chestBoatLayer));
            }

            // Register the woodtype for custom signs.
            for (var sign : WoodRegistry.SIGNS) {
                var feature = sign.get().feature();
                var woodType = sign.get().material().woodType();

                Sheets.SIGN_MATERIALS.put(woodType, new Material(Sheets.SIGN_SHEET, feature.registryId("entity/signs/" + woodType.name())));
                Sheets.HANGING_SIGN_MATERIALS.put(woodType, new Material(Sheets.SIGN_SHEET, feature.registryId("entity/signs/hanging/" + woodType.name())));
            }

            // Add all custom items to creative tabs.
            var table = WoodRegistry.ITEM_CREATIVE_TABS;
            var map = table.rowMap();

            for (var item : map.keySet()) {
                for (var entry : map.get(item).entrySet()) {
                    var after = entry.getKey();
                    var customType = entry.getValue();
                    List<ResourceKey<CreativeModeTab>> tabs = new ArrayList<>();

                    if (CustomWood.BUILDING_BLOCKS.contains(customType)) {
                        tabs.add(CreativeModeTabs.BUILDING_BLOCKS);
                    }
                    if (CustomWood.FUNCTIONAL_BLOCKS.contains(customType)) {
                        tabs.add(CreativeModeTabs.FUNCTIONAL_BLOCKS);
                    }
                    if (CustomWood.NATURAL_BLOCKS.contains(customType)) {
                        tabs.add(CreativeModeTabs.NATURAL_BLOCKS);
                    }
                    if (CustomWood.TOOLS_AND_UTILITIES.contains(customType)) {
                        tabs.add(CreativeModeTabs.TOOLS_AND_UTILITIES);
                    }

                    for (var tab : tabs) {
                        clientRegistry.itemTab(item.get(), tab, after.get());
                    }
                }
            }
        };
    }
}
