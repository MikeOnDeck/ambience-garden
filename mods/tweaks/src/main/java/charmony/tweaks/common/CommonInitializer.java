package charmony.tweaks.common;

import charmony.api.core.Side;
import charmony.tweaks.TweaksMod;
import charmony.tweaks.common.features.animal_armor_grinding.AnimalArmorGrinding;
import charmony.tweaks.common.features.animal_damage_immunity.AnimalDamageImmunity;
import charmony.tweaks.common.features.animal_reviving.AnimalReviving;
import charmony.tweaks.common.features.campfires_heal_players.CampfiresHealPlayers;
import charmony.tweaks.common.features.chiseled_bookshelves_show_book_on_hover.ChiseledBookshelvesShowBookOnHover;
import charmony.tweaks.common.features.compact_recipes.CompactRecipes;
import charmony.tweaks.common.features.crop_feather_falling.CropFeatherFalling;
import charmony.tweaks.common.features.crop_replanting.CropReplanting;
import charmony.tweaks.common.features.deepslate_dungeons.DeepslateDungeons;
import charmony.tweaks.common.features.grindstone_disenchanting.GrindstoneDisenchanting;
import charmony.tweaks.common.features.item_frame_hiding.ItemFrameHiding;
import charmony.tweaks.common.features.item_repairing.ItemRepairing;
import charmony.tweaks.common.features.item_restocking.ItemRestocking;
import charmony.tweaks.common.features.mineshaft_improvements.MineshaftImprovements;
import charmony.tweaks.common.features.mob_drops.MobDrops;
import charmony.tweaks.common.features.nether_portal_blocks.NetherPortalBlocks;
import charmony.tweaks.common.features.parrots_stay_on_shoulder.ParrotsStayOnShoulder;
import charmony.tweaks.common.features.path_converting.PathConverting;
import charmony.tweaks.common.features.piglin_pointing.PiglinPointing;
import charmony.tweaks.common.features.pigs_find_mushrooms.PigsFindMushrooms;
import charmony.tweaks.common.features.repair_cost_unlimited.RepairCostUnlimited;
import charmony.tweaks.common.features.respawn_anchors_work_everywhere.RespawnAnchorsWorkEverywhere;
import charmony.tweaks.common.features.shulker_box_transferring.ShulkerBoxTransferring;
import charmony.tweaks.common.features.shulker_boxes_show_contents.ShulkerBoxesShowContents;
import charmony.tweaks.common.features.spawners_drop_items.SpawnersDropItems;
import charmony.tweaks.common.features.suspicious_block_creating.SuspiciousBlockCreating;
import charmony.tweaks.common.features.torchflowers_emit_light.TorchflowersEmitLight;
import charmony.tweaks.common.features.totems_work_from_inventory.TotemsWorkFromInventory;
import charmony.tweaks.common.features.trade_improvements.TradeImprovements;
import charmony.tweaks.common.features.villager_attracting.VillagerAttracting;
import charmony.tweaks.common.features.wandering_trader_tiers.WanderingTraderTiers;
import net.fabricmc.api.ModInitializer;

import java.util.List;

public class CommonInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // Ensure charmony is launched first.
        charmony.core.common.CommonInitializer.init();

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
            ShulkerBoxesShowContents.class,
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
