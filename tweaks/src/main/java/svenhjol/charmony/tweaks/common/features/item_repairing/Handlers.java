package svenhjol.charmony.tweaks.common.features.item_repairing;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import svenhjol.charmony.core.base.Setup;

public class Handlers extends Setup<ItemRepairing> {
    public Handlers(ItemRepairing feature) {
        super(feature);
    }

    @SuppressWarnings({"RedundantIfStatement", "unused"})
    public boolean anvilRepair(AnvilMenu menu, Player player, ItemStack leftStack, ItemStack rightStack) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        if (feature().repairTridents() && leftStack.is(Items.TRIDENT) && rightStack.is(Items.PRISMARINE_SHARD)) {
            return true;
        }

        if (feature().repairElytra() && !serverLevel.getGameRules().getBoolean(GameRules.RULE_DOINSOMNIA)
            && leftStack.is(Items.ELYTRA) && rightStack.is(Items.LEATHER)) {
            return true;
        }

        if (feature().repairNetheriteItems() && leftStack.is(Tags.REPAIRABLE_USING_SCRAP) && rightStack.is(Items.NETHERITE_SCRAP)) {
            return true;
        }

        return false;
    }

    public void anvilOnTake(Player player, ItemStack original, ItemStack material, ItemStack taken) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (original.is(Items.TRIDENT) && taken.is(Items.TRIDENT)) {
            if (original.getDamageValue() > taken.getDamageValue()) {
                feature().advancements.repairedTrident(serverPlayer);
            }
        } else if (original.is(Tags.REPAIRABLE_USING_SCRAP) && taken.is(Tags.REPAIRABLE_USING_SCRAP)) {
            if (original.getDamageValue() > taken.getDamageValue()) {
                feature().advancements.repairedNetherite(serverPlayer);
            }
        }
    }
}
