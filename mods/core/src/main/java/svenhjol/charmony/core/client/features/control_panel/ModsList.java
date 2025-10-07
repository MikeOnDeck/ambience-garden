package svenhjol.charmony.core.client.features.control_panel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import svenhjol.charmony.core.Charmony;
import svenhjol.charmony.core.base.Mod;

import java.util.*;

public class ModsList extends AbstractSelectionList<ModsList.Entry> {
    private final ModsScreen parent;
    private final List<Entry> entries = new ArrayList<>();
    private final List<Mod> mods = new LinkedList<>();
    private final Map<String, WidgetSprites> modIconButtons = new HashMap<>();

    public ModsList(Minecraft minecraft, ModsScreen parent) {
        super(minecraft, parent.width, parent.layout().getContentHeight(), parent.layout().getHeaderHeight(), 25);
        this.parent = parent;

        for (var mod : mods()) {
            modIconButtons.put(mod.id(), makeIconButton(mod));

            var entry = new Entry(mod);
            entries.add(entry);
            addEntry(entry); // The vanilla method.
        }
    }

    public void refreshState() {
        entries.forEach(Entry::refreshState);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // no op
    }

    @Override
    protected int contentHeight() {
        return super.contentHeight() + SettingsScreen.CONTENT_BOTTOM_MARGIN;
    }

    @Override
    public int getRowWidth() {
        return SettingsScreen.CONTENT_ROW_WIDTH;
    }

    protected List<Mod> mods() {
        if (mods.isEmpty()) {
            mods.addAll(Mod.all());
            mods.sort(Comparator.comparing(Mod::name));
        }
        return mods;
    }

    public class Entry extends AbstractSelectionList.Entry<Entry> {
        private final Mod mod;
        private final ImageButton modIconButton;
        private final Button modNameButton;

        public Entry(Mod mod) {
            this.mod = mod;
            this.modNameButton = Button.builder(Component.literal(mod.name()),
                button -> configure()).build();
            this.modIconButton = new ImageButton(0, 0, 18, 18,
                modIconButtons.get(mod.id()), button -> configure());

            Tooltip modTooltip = Tooltip.create(Component.literal(mod.description()));
            this.modNameButton.setTooltip(modTooltip);
            this.modIconButton.setTooltip(modTooltip);
        }

        public void refreshState() {
            // no op, yet
        }

        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean bl, float tickDelta) {
            int y = this.getY();
            y += SettingsScreen.CONTENT_TOP_MARGIN;

            int nameButtonX = ModsList.this.width / 2 - (modNameButton.getWidth() / 2) + 10;
            int nameButtonY = y - 2;

            // Name button.
            modNameButton.setPosition(nameButtonX, nameButtonY);
            modNameButton.render(guiGraphics, mouseX, mouseY, tickDelta);

            // Mod icon next to the button.
            modIconButton.setPosition(nameButtonX - 22, nameButtonY + 1);
            modIconButton.render(guiGraphics, mouseX, mouseY, tickDelta);
        }

        /**
         * We must implement our own behavior here or the scrolling causes erroneous button clicks.
         */
        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
            var mouseX = event.x();
            var mouseY = event.y();

            if (modNameButton.isMouseOver(mouseX, mouseY)) {
                modNameButton.mouseClicked(event, bl);
                return false;
            }

            return super.mouseClicked(event, bl);
        }

        private void configure() {
            minecraft.setScreen(new FeaturesScreen(mod, parent));
        }
    }

    protected static WidgetSprites makeIconButton(Mod mod) {
        return new WidgetSprites(
            Charmony.id(mod.id()),
            Charmony.id(mod.id())
        );
    }
}
