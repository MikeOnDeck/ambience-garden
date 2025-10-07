package svenhjol.charmony.core.client.features.control_panel;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import svenhjol.charmony.api.core.Color;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.core.base.Feature;
import svenhjol.charmony.core.base.Log;
import svenhjol.charmony.core.helpers.ConfigHelper;
import svenhjol.charmony.core.helpers.TextComponentHelper;
import svenhjol.charmony.core.helpers.TextHelper;

import java.lang.reflect.Field;
import java.util.*;

public class FeatureConfigList extends AbstractSelectionList<FeatureConfigList.Entry> {
    private static final Log LOGGER = new Log("FeatureConfigList");
    private final Feature feature;
    private final FeatureConfigScreen parent;
    private final Map<Field, Entry> entries = new HashMap<>();
    private final Map<Field, Object> newValues = new HashMap<>();
    private boolean requiresRestart = false;
    private int extraScrollHeight = 0;

    public FeatureConfigList(Feature feature, Minecraft minecraft, int width, FeatureConfigScreen parent) {
        super(minecraft, width, parent.layout().getContentHeight() - parent.layout().getFooterHeight() + 10, parent.layout().getHeaderHeight(), 25);
        this.feature = feature;
        this.parent = parent;
        this.readConfig();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // no op
    }

    @Override
    public int getRowWidth() {
        return 310;
    }

    @Override
    protected int contentHeight() {
        return super.contentHeight() + extraScrollHeight + SettingsScreen.CONTENT_BOTTOM_MARGIN;
    }

    public void readConfig() {
        for (var side : feature.sides()) {
            var fields = new ArrayList<>(Arrays.asList(side.getClass().getDeclaredFields()));
            for (var field : fields) {
                try {
                    var annotation = field.getDeclaredAnnotation(Configurable.class);
                    if (annotation == null) continue;

                    Entry entry;
                    field.setAccessible(true);
                    var name = annotation.name();
                    var description = annotation.description();
                    var fieldValue = field.get(null);

                    entry = switch (fieldValue) {
                        case Boolean ignored -> new BooleanEntry(field, name, description);
                        case Integer ignored -> new IntegerEntry(field, name, description);
                        case Double ignored -> new DoubleEntry(field, name, description);
                        case String ignored -> new StringEntry(field, name, description);
                        case List<?> ignored -> new StringListEntry(field, name, description);
                        case null, default -> null;
                    };

                    if (entry != null) {
                        entries.put(field, entry);
                        addEntry(entry);
                    }

                } catch (Exception e) {
                    LOGGER.error("Could not read field " + field + ": " + e.getMessage());
                }
            }
        }
    }

    public void writeConfig() {
        for (var entry : newValues.entrySet()) {
            var field = entry.getKey();
            var val = entry.getValue();
            ConfigHelper.setField(field, val);
        }

        var mod = feature.mod();
        mod.config().write();
    }

    public void defaults() {
        newValues.clear();
        for (var entry : entries.values()) {
            entry.resetToDefault();
        }
    }

    public boolean requiresRestart() {
        return requiresRestart;
    }

    public class BooleanEntry extends Entry {
        private final Button enabledButton = Button.builder(
            Component.translatable("gui.charmony.settings.true"), b -> disable()).width(60).build();
        private final Button disabledButton = Button.builder(
            Component.translatable("gui.charmony.settings.false"), b -> enable()).width(60).build();

        public BooleanEntry(Field field, String label, String description) {
            super(field, label, description);
            addChildren(enabledButton, disabledButton);
            refreshState();
        }

        private void refreshState() {
            if (val instanceof Boolean b) {
                this.enabledButton.visible = b;
                this.disabledButton.visible = !b;
            } else {
                this.enabledButton.visible = true;
                this.enabledButton.active = false;
                this.disabledButton.visible = false;
            }
        }

        public void enable() {
            updateValue(true);
        }

        public void disable() {
            updateValue(false);
        }

        @Override
        protected void afterUpdate() {
            refreshState();
        }

        @Override
        public void resetToDefault() {
            super.resetToDefault();
            refreshState();
        }

        @Override
        public void renderChild(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
            var buttonX = FeatureConfigList.this.scrollBarX() - enabledButton.getWidth() - 10;
            var buttonY = this.getY() + 2;

            enabledButton.setPosition(buttonX, buttonY);
            enabledButton.render(guiGraphics, mouseX, mouseY, tickDelta);

            disabledButton.setPosition(buttonX, buttonY);
            disabledButton.render(guiGraphics, mouseX, mouseY, tickDelta);
        }
    }

    public class IntegerEntry extends SingleTextBoxEntry {
        public IntegerEntry(Field field, String label, String description) {
            super(field, label, description);
        }

        @Override
        protected int maxLength() {
            return 8;
        }

        @Override
        protected void onChange(String newVal) {
            int parsed;
            try {
                parsed = Integer.parseInt(newVal);
            } catch (NumberFormatException e) {
                if (defaultVal == null) {
                    LOGGER.error("Could not get default value for field " + field);
                    return;
                }
                parsed = (int)defaultVal;
            }

            updateValue(parsed);
        }
    }

    public class DoubleEntry extends SingleTextBoxEntry {
        public DoubleEntry(Field field, String label, String description) {
            super(field, label, description);
        }

        @Override
        protected int maxLength() {
            return 16;
        }

        @Override
        protected void onChange(String newVal) {
            double parsed;
            try {
                parsed = Double.parseDouble(newVal);
            } catch (NumberFormatException e) {
                if (defaultVal == null) {
                    LOGGER.error("Could not get default value for field " + field);
                    return;
                }
                parsed = (double)defaultVal;
            }

            updateValue(parsed);
        }
    }

    public class StringEntry extends SingleTextBoxEntry {
        public StringEntry(Field field, String label, String description) {
            super(field, label, description);
        }

        @Override
        protected int maxLength() {
            return 128;
        }

        @Override
        protected int width() {
            return 120;
        }

        @Override
        protected void onChange(String newVal) {
            String parsed;
            try {
                parsed = String.valueOf(newVal);
            } catch (NumberFormatException e) {
                if (defaultVal == null) {
                    LOGGER.error("Could not get default value for field " + field);
                    return;
                }
                parsed = (String)defaultVal;
            }

            updateValue(parsed);
        }
    }

    public class StringListEntry extends MultiLineTextBoxEntry {
        public StringListEntry(Field field, String label, String description) {
            super(field, label, description);
        }

        @Override
        protected int maxLength() {
            return 1000;
        }

        @Override
        protected void onChange(String newVal) {
            List<String> parsed;
            try {
                var asString = String.valueOf(newVal);
                parsed = Arrays.stream(asString.split("\n")).filter(s -> !s.isEmpty()).toList();
            } catch (NumberFormatException e) {
                if (defaultVal == null) {
                    LOGGER.error("Could not get default value for field " + field);
                    return;
                }
                parsed = Arrays.asList(((String)defaultVal).split("\n"));
            }

            updateValue(parsed);
        }
    }

    public abstract class SingleTextBoxEntry extends Entry {
        private final EditBox inputBox;

        public SingleTextBoxEntry(Field field, String label, String description) {
            super(field, label, description);
            var font = FeatureConfigList.this.minecraft.font;

            inputBox = new EditBox(font, 0, 0, width() - 1, 15, Component.empty());
            inputBox.setTextColor(-1);
            inputBox.setTextColorUneditable(-1);
            inputBox.setMaxLength(maxLength());
            inputBox.setValue(String.valueOf(val));
            inputBox.setResponder(this::onChange);
            inputBox.visible = false;
            addChildren(inputBox);
        }

        protected int width() {
            return 60;
        }

        protected abstract int maxLength();

        protected abstract void onChange(String newVal);

        @Override
        public void resetToDefault() {
            super.resetToDefault();
            inputBox.setValue(String.valueOf(val));
        }

        @Override
        public void renderChild(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
            var boxX = FeatureConfigList.this.scrollBarX() - inputBox.getWidth() - 10;
            var boxY = this.getY() + 5;
            inputBox.visible = true;
            inputBox.setPosition(boxX, boxY);
            inputBox.renderWidget(guiGraphics, mouseX, mouseY, tickDelta);
        }
    }

    public abstract class MultiLineTextBoxEntry extends Entry {
        private final MultiLineEditBox inputBox;

        public MultiLineTextBoxEntry(Field field, String label, String description) {
            super(field, label, description);
            var font = FeatureConfigList.this.minecraft.font;
            var textColor = new Color(0xffffff);
            inputBox = new MultiLineEditBox(font, 0, 0, width() - 1, height(), CommonComponents.EMPTY, CommonComponents.EMPTY, textColor.getArgbColor(), false, 0xffff00, true, true);
            inputBox.setValueListener(this::onChange);
            inputBox.setCharacterLimit(maxLength());
            inputBox.visible = false;
            refreshState();
            addChildren(inputBox);
            addExtraScrollHeight(height() + 20); // Add margin for the text counter
        }

        @SuppressWarnings("unchecked")
        protected void refreshState() {
            var sb = new StringBuilder();
            var lines = (List<String>)val;
            for (String item : lines) {
                sb.append(item).append("\n");
            }
            inputBox.setValue(sb.toString());
        }

        protected int width() {
            return getRowWidth() - 3;
        }

        protected int height() {
            return 60;
        }

        protected abstract int maxLength();

        protected abstract void onChange(String newVal);

        @Override
        public void resetToDefault() {
            super.resetToDefault();
            refreshState();
        }

        @Override
        public void renderChild(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
            var layout = FeatureConfigList.this.parent.layout();
            var boxX = FeatureConfigList.this.scrollBarX() - getRowWidth() - 4;
            var boxY = this.getY() + 5;
            inputBox.visible = true;
            inputBox.setHeight(Mth.clamp(layout.getContentHeight() - mouseY, 0, 60)); // Dumb z-index hack
            inputBox.setPosition(boxX, boxY);
            inputBox.render(guiGraphics, mouseX, mouseY, tickDelta);
        }
    }

    public abstract class Entry extends AbstractSelectionList.Entry<Entry> {
        protected final Field field;
        protected final Configurable annotation;
        protected final String label;
        protected final List<Component> tooltip;

        protected Object defaultVal;
        protected Object val;
        protected boolean isUpdating = false;

        public Entry(Field field, String label, String description) {
            this.field = field;
            this.label = label;
            this.tooltip = TextComponentHelper.toComponents(description, 48);
            this.annotation = field.getDeclaredAnnotation(Configurable.class);

            // Try and get the default value for this config item.
            defaultVal = FeatureConfigList.this.feature.mod().config().defaultValue(field).orElse(null);
            val = ConfigHelper.getField(field, null);

            tooltip.addFirst(Component.literal(label).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));

            if (defaultVal != null) {
                var out = TextHelper.truncateWithEllipsis(String.valueOf(defaultVal), 32);
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("gui.charmony.settings.default_value", out)
                    .withStyle(ChatFormatting.AQUA));
            }

            if (annotation != null && annotation.requireRestart()) {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("gui.charmony.settings.requires_restart")
                    .withStyle(ChatFormatting.RED));
            }
        }

        protected void updateValue(Object val) {
            if (isUpdating) return;

            FeatureConfigList.this.newValues.put(field, val);
            this.val = val;

            if (annotation.requireRestart()) {
                FeatureConfigList.this.requiresRestart = true;
            }

            isUpdating = true;
            afterUpdate();
            isUpdating = false;
        }

        /**
         * Hook called immediately after anything updates the widget.
         * Be careful when using textboxes as input can be ignored/overridden immediately.
         */
        protected void afterUpdate() {
            // hook
        }

        public void resetToDefault() {
            updateValue(defaultVal);
        }

        public void addChildren(AbstractWidget... widgets) {
            FeatureConfigList.this.parent.addChildren(widgets);
        }

        public void addExtraScrollHeight(int height) {
            FeatureConfigList.this.extraScrollHeight += height;
        }

        public abstract void renderChild(GuiGraphics guiGraphics, int x, int y, float tickDelta);

        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean bl, float f) {
            int x = this.getX();
            int y = this.getY();
            y += SettingsScreen.CONTENT_TOP_MARGIN;

            var font = FeatureConfigList.this.minecraft.font;
            var isDefaultVal = defaultVal.equals(val);
            var label = Component.literal(this.label);
            var labelWidth = font.width(label);
            var textLeft = x + 5;
            var textTop = y + 2;

            label = label.withColor(isDefaultVal ? 0xefefef : 0xffff00);
            guiGraphics.drawString(font, label, x + 5, y + 2, -1);

            if (mouseX >= textLeft && mouseX <= textLeft + labelWidth
                && mouseY >= textTop && mouseY <= textTop + 6) {
                guiGraphics.setComponentTooltipForNextFrame(font, tooltip, mouseX, mouseY);
            }

            renderChild(guiGraphics, mouseX, mouseY, f);
        }
    }
}
