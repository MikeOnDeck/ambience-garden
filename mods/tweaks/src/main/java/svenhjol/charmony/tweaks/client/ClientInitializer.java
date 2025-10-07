package svenhjol.charmony.tweaks.client;

import net.fabricmc.api.ClientModInitializer;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.tweaks.TweaksMod;
import svenhjol.charmony.tweaks.client.features.burning_has_reduced_view_blocking.BurningHasReducedViewBlocking;
import svenhjol.charmony.tweaks.client.features.chiseled_bookshelves_show_book_on_hover.ChiseledBookshelvesShowBookOnHover;
import svenhjol.charmony.tweaks.client.features.compasses_show_position.CompassesShowPosition;
import svenhjol.charmony.tweaks.client.features.crafting_table_nearby.CraftingTableNearby;
import svenhjol.charmony.tweaks.client.features.grindstone_disenchanting.GrindstoneDisenchanting;
import svenhjol.charmony.tweaks.client.features.item_frame_hiding.ItemFrameHiding;
import svenhjol.charmony.tweaks.client.features.item_tidying.ItemTidying;
import svenhjol.charmony.tweaks.client.features.jukeboxes_stop_background_music.JukeboxesStopBackgroundMusic;
import svenhjol.charmony.tweaks.client.features.maps_show_when_hovering.MapsShowWhenHovering;
import svenhjol.charmony.tweaks.client.features.mob_textures.MobTextures;
import svenhjol.charmony.tweaks.client.features.piglin_pointing.PiglinPointing;
import svenhjol.charmony.tweaks.client.features.pigs_find_mushrooms.PigsFindMushrooms;
import svenhjol.charmony.tweaks.client.features.repair_cost_unlimited.RepairCostUnlimited;
import svenhjol.charmony.tweaks.client.features.repair_cost_visible.RepairCostVisible;
import svenhjol.charmony.tweaks.client.features.shields_have_reduced_view_blocking.ShieldsHaveReducedViewBlocking;
import svenhjol.charmony.tweaks.client.features.shulker_box_menu_colors.ShulkerBoxMenuColors;
import svenhjol.charmony.tweaks.client.features.shulker_box_transferring.ShulkerBoxTransferring;
import svenhjol.charmony.tweaks.client.features.shulker_boxes_show_contents_when_hovering.ShulkerBoxesShowContentsWhenHovering;
import svenhjol.charmony.tweaks.client.features.spyglass_scope_hiding.SpyglassScopeHiding;
import svenhjol.charmony.tweaks.client.features.telemetry.Telemetry;
import svenhjol.charmony.tweaks.client.features.totem_emergency_swap.TotemEmergencySwap;

import java.util.List;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Ensure charmony is launched first.
        svenhjol.charmony.core.client.ClientInitializer.init();

        // Prepare and run the mod.
        var tweaks = TweaksMod.instance();
        tweaks.addSidedFeatures(List.of(
            BurningHasReducedViewBlocking.class,
            ChiseledBookshelvesShowBookOnHover.class,
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
            ShulkerBoxesShowContentsWhenHovering.class,
            SpyglassScopeHiding.class,
            Telemetry.class,
            TotemEmergencySwap.class
        ));
        tweaks.run(Side.Client);
    }
}
