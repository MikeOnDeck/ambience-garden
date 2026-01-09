package charmony.tweaks.client;

import charmony.api.core.Side;
import charmony.tweaks.TweaksMod;
import charmony.tweaks.client.features.burning_has_reduced_view_blocking.BurningHasReducedViewBlocking;
import charmony.tweaks.client.features.chiseled_bookshelves_show_book.ChiseledBookshelvesShowBook;
import charmony.tweaks.client.features.compasses_show_position.CompassesShowPosition;
import charmony.tweaks.client.features.crafting_table_nearby.CraftingTableNearby;
import charmony.tweaks.client.features.grindstone_disenchanting.GrindstoneDisenchanting;
import charmony.tweaks.client.features.item_frame_hiding.ItemFrameHiding;
import charmony.tweaks.client.features.item_tidying.ItemTidying;
import charmony.tweaks.client.features.jukeboxes_stop_background_music.JukeboxesStopBackgroundMusic;
import charmony.tweaks.client.features.maps_show_when_hovering.MapsShowWhenHovering;
import charmony.tweaks.client.features.mob_textures.MobTextures;
import charmony.tweaks.client.features.piglin_pointing.PiglinPointing;
import charmony.tweaks.client.features.pigs_find_mushrooms.PigsFindMushrooms;
import charmony.tweaks.client.features.repair_cost_unlimited.RepairCostUnlimited;
import charmony.tweaks.client.features.repair_cost_visible.RepairCostVisible;
import charmony.tweaks.client.features.shields_have_reduced_view_blocking.ShieldsHaveReducedViewBlocking;
import charmony.tweaks.client.features.shulker_box_menu_colors.ShulkerBoxMenuColors;
import charmony.tweaks.client.features.shulker_box_transferring.ShulkerBoxTransferring;
import charmony.tweaks.client.features.shulker_boxes_show_contents.ShulkerBoxesShowContents;
import charmony.tweaks.client.features.spyglass_scope_hiding.SpyglassScopeHiding;
import charmony.tweaks.client.features.telemetry_disable.TelemetryDisable;
import charmony.tweaks.client.features.totem_emergency_swap.TotemEmergencySwap;
import net.fabricmc.api.ClientModInitializer;

import java.util.List;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Ensure charmony is launched first.
        charmony.core.client.ClientInitializer.init();

        // Prepare and run the mod.
        var tweaks = TweaksMod.instance();
        tweaks.addSidedFeatures(List.of(
            BurningHasReducedViewBlocking.class,
            ChiseledBookshelvesShowBook.class,
            CompassesShowPosition.class,
            CraftingTableNearby.class,
            GrindstoneDisenchanting.class,
            ItemFrameHiding.class,
            ItemTidying.class,
            JukeboxesStopBackgroundMusic.class,
            MapsShowWhenHovering.class,
            MobTextures.class,
            PiglinPointing.class,
            PigsFindMushrooms.class,
            RepairCostUnlimited.class,
            RepairCostVisible.class,
            ShieldsHaveReducedViewBlocking.class,
            ShulkerBoxMenuColors.class,
            ShulkerBoxTransferring.class,
            ShulkerBoxesShowContents.class,
            SpyglassScopeHiding.class,
            TelemetryDisable.class,
            TotemEmergencySwap.class
        ));
        tweaks.run(Side.Client);
    }
}
