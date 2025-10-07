package svenhjol.charmony.tweaks.common;

import net.fabricmc.api.ModInitializer;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.tweaks.TweaksMod;
import svenhjol.charmony.tweaks.common.features.animal_armor_grinding.AnimalArmorGrinding;
import svenhjol.charmony.tweaks.common.features.animal_damage_immunity.AnimalDamageImmunity;
import svenhjol.charmony.tweaks.common.features.animal_reviving.AnimalReviving;
import svenhjol.charmony.tweaks.common.features.campfires_heal_players.CampfiresHealPlayers;
import svenhjol.charmony.tweaks.common.features.chiseled_bookshelves_show_book_on_hover.ChiseledBookshelvesShowBookOnHover;
import svenhjol.charmony.tweaks.common.features.compact_recipes.CompactRecipes;
import svenhjol.charmony.tweaks.common.features.crop_feather_falling.CropFeatherFalling;
import svenhjol.charmony.tweaks.common.features.crop_replanting.CropReplanting;
import svenhjol.charmony.tweaks.common.features.deepslate_dungeons.DeepslateDungeons;
import svenhjol.charmony.tweaks.common.features.grindstone_disenchanting.GrindstoneDisenchanting;
import svenhjol.charmony.tweaks.common.features.item_frame_hiding.ItemFrameHiding;
import svenhjol.charmony.tweaks.common.features.item_repairing.ItemRepairing;
import svenhjol.charmony.tweaks.common.features.item_restocking.ItemRestocking;
import svenhjol.charmony.tweaks.common.features.mineshaft_improvements.MineshaftImprovements;
import svenhjol.charmony.tweaks.common.features.mob_drops.MobDrops;
import svenhjol.charmony.tweaks.common.features.nether_portal_blocks.NetherPortalBlocks;
import svenhjol.charmony.tweaks.common.features.parrots_stay_on_shoulder.ParrotsStayOnShoulder;
import svenhjol.charmony.tweaks.common.features.path_converting.PathConverting;
import svenhjol.charmony.tweaks.common.features.piglin_pointing.PiglinPointing;
import svenhjol.charmony.tweaks.common.features.pigs_find_mushrooms.PigsFindMushrooms;
import svenhjol.charmony.tweaks.common.features.repair_cost_unlimited.RepairCostUnlimited;
import svenhjol.charmony.tweaks.common.features.respawn_anchors_work_everywhere.RespawnAnchorsWorkEverywhere;
import svenhjol.charmony.tweaks.common.features.shulker_box_transferring.ShulkerBoxTransferring;
import svenhjol.charmony.tweaks.common.features.spawners_drop_items.SpawnersDropItems;
import svenhjol.charmony.tweaks.common.features.suspicious_block_creating.SuspiciousBlockCreating;
import svenhjol.charmony.tweaks.common.features.torchflowers_emit_light.TorchflowersEmitLight;
import svenhjol.charmony.tweaks.common.features.totems_work_from_inventory.TotemsWorkFromInventory;
import svenhjol.charmony.tweaks.common.features.trade_improvements.TradeImprovements;
import svenhjol.charmony.tweaks.common.features.villager_attracting.VillagerAttracting;
import svenhjol.charmony.tweaks.common.features.wandering_trader_tiers.WanderingTraderTiers;

import java.util.List;

public class CommonInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // Ensure charmony is launched first.
        svenhjol.charmony.core.common.CommonInitializer.init();

        // Prepare and run the mod.
        var tweaks = TweaksMod.instance();
        tweaks.addSidedFeatures(List.of(
            AnimalArmorGrinding.class,
            AnimalDamageImmunity.class,
            AnimalReviving.class,
            CampfiresHealPlayers.class,
            ChiseledBookshelvesShowBookOnHover.class,
            CompactRecipes.class,
            CropFeatherFalling.class,
            CropReplanting.class,
            DeepslateDungeons.class,
            GrindstoneDisenchanting.class,
            ItemFrameHiding.class,
            ItemRepairing.class,
            ItemRestocking.class,
            MineshaftImprovements.class,
            MobDrops.class,
            NetherPortalBlocks.class,
            ParrotsStayOnShoulder.class,
            PathConverting.class,
            PiglinPointing.class,
            PigsFindMushrooms.class,
            RepairCostUnlimited.class,
            RespawnAnchorsWorkEverywhere.class,
            ShulkerBoxTransferring.class,
            SpawnersDropItems.class,
            SuspiciousBlockCreating.class,
            TorchflowersEmitLight.class,
            TotemsWorkFromInventory.class,
            TradeImprovements.class,
            VillagerAttracting.class,
            WanderingTraderTiers.class
        ));
        tweaks.run(Side.Common);
    }
}
