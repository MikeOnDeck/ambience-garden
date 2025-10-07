package svenhjol.charmony.api.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Adds custom behavior to vanilla anvils.
 */
@SuppressWarnings("unused")
public final class AnvilEvents {
    public static final OnTakeEvent ON_TAKE = new OnTakeEvent();
    public static final RepairEvent REPAIR = new RepairEvent();
    public static final UpdateEvent UPDATE = new UpdateEvent();

    public static class OnTakeEvent extends CharmonyEvent<OnTakeEvent.Handler> {
        private OnTakeEvent() {}

        public void invoke(Player player, ItemStack original, ItemStack material, ItemStack taken) {
            for (var handler : getHandlers()) {
                handler.run(player, original, material, taken);
            }
        }

        @FunctionalInterface
        public interface Handler {
            void run(Player player, ItemStack original, ItemStack material, ItemStack taken);
        }
    }

    public static class RepairEvent extends CharmonyEvent<RepairEvent.Handler> {
        private RepairEvent() {}

        public boolean invoke(AnvilMenu menu, Player player, ItemStack leftStack, ItemStack rightStack) {
            for (var handler : getHandlers()) {
                var result = handler.run(menu, player, leftStack, rightStack);

                // Return true as soon as a handler satisfies the criteria.
                if (result) {
                    return true;
                }
            }

            return false;
        }

        @FunctionalInterface
        public interface Handler {
            boolean run(AnvilMenu menu, Player player, ItemStack leftStack, ItemStack rightStack);
        }
    }

    public static class UpdateEvent extends CharmonyEvent<UpdateEvent.Handler> {
        private UpdateEvent() {}

        public Optional<AnvilRecipe> invoke(Player player, ItemStack input, ItemStack material, long cost) {
            for (var handler : getHandlers()) {
                var result = handler.run(player, input, material, cost);

                // If an AnvilRecipe has been provided then stop processing.
                if (result.isPresent()) {
                    return result;
                }
            }

            return Optional.empty();
        }

        @FunctionalInterface
        public interface Handler {
            Optional<AnvilRecipe> run(Player player, ItemStack input, ItemStack material, long cost);
        }
    }

    public static class AnvilRecipe {
        public ItemStack output;
        public int experienceCost;
        public int materialCost;
    }
}
