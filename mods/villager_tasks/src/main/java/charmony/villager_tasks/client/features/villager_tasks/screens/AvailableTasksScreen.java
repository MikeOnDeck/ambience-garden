package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.villager_tasks.client.features.villager_tasks.Resources;
import net.minecraft.world.item.trading.Merchant;

public class AvailableTasksScreen extends BaseScreen {
    public AvailableTasksScreen(Merchant merchant) {
        super(Resources.AVAILABLE_TASKS);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
