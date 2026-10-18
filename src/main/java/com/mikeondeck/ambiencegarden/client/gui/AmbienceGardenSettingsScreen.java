package com.mikeondeck.ambiencegarden.client.gui;

import com.mikeondeck.ambiencegarden.client.features.biomes.Biomes;
import com.mikeondeck.ambiencegarden.client.features.environment.Environment;
import com.mikeondeck.ambiencegarden.client.features.settings.Settings;
import com.mikeondeck.ambiencegarden.client.sound.AmbientSoundIds;
import com.mikeondeck.ambiencegarden.core.base.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Vanilla options-list screen for ambient sound settings.
 * Every control is a button, cycle button, or slider so a controller can use it.
 */
public class AmbienceGardenSettingsScreen extends OptionsSubScreen {
    private static final Log LOG = new Log(AmbientSoundIds.NAMESPACE, "Settings");
    private static final List<String> AUDIO_CHANNELS = List.of(
        "music", "record", "weather", "block", "hostile", "neutral", "player", "ambient", "voice"
    );
    private static final List<String> VANILLA_DIMENSIONS = List.of(
        "minecraft:overworld",
        "minecraft:the_nether",
        "minecraft:the_end"
    );

    public AmbienceGardenSettingsScreen(Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.translatable("gui.ambience_garden.settings.title"));
    }

    @Override
    protected void addOptions() {
        var list = this.list;
        if (list == null) return;

        var biomes = Biomes.feature();
        var environment = Environment.feature();
        var settings = Settings.feature();

        list.addHeader(Component.translatable("options.ambience_garden.section.playback"));
        list.addSmall(
            featureToggle("options.ambience_garden.biomes", "options.ambience_garden.biomes.tooltip", biomes.enabled(), biomes::enabled),
            featureToggle("options.ambience_garden.environment", "options.ambience_garden.environment.tooltip", environment.enabled(), environment::enabled)
        );
        list.addSmall(audioChannelButton(settings), null);
        list.addBig(volumeSlider(
            "options.ambience_garden.biome_volume",
            "options.ambience_garden.biome_volume.tooltip",
            settings.biomeVolumeScaling(),
            settings::setBiomeVolumeScaling
        ));
        list.addBig(volumeSlider(
            "options.ambience_garden.environment_volume",
            "options.ambience_garden.environment_volume.tooltip",
            settings.environmentVolumeScaling(),
            settings::setEnvironmentVolumeScaling
        ));
        list.addBig(intSlider(
            "options.ambience_garden.cull_height",
            "options.ambience_garden.cull_height.tooltip",
            0,
            256,
            settings.cullSoundAboveGround(),
            settings::setCullSoundAboveGround
        ));
        list.addBig(intSlider(
            "options.ambience_garden.biome_blend",
            "options.ambience_garden.biome_blend.tooltip",
            0,
            256,
            biomes.biomeBlend(),
            biomes::setBiomeBlend
        ));

        list.addHeader(Component.translatable("options.ambience_garden.section.biome_dimensions"));
        addDimensionToggles(biomes.dimensions(), biomes::setDimensionEnabled);

        list.addHeader(Component.translatable("options.ambience_garden.section.environment"));
        list.addSmall(
            soundToggle("options.ambience_garden.bleak", "options.ambience_garden.bleak.tooltip", environment.bleak(), environment::setBleak),
            soundToggle("options.ambience_garden.dry", "options.ambience_garden.dry.tooltip", environment.dry(), environment::setDry),
            soundToggle("options.ambience_garden.high", "options.ambience_garden.high.tooltip", environment.high(), environment::setHigh),
            soundToggle("options.ambience_garden.night_plains", "options.ambience_garden.night_plains.tooltip", environment.nightPlains(), environment::setNightPlains),
            soundToggle("options.ambience_garden.snowstorm", "options.ambience_garden.snowstorm.tooltip", environment.snowstorm(), environment::setSnowstorm),
            soundToggle("options.ambience_garden.village", "options.ambience_garden.village.tooltip", environment.village(), environment::setVillage),
            soundToggle("options.ambience_garden.alien", "options.ambience_garden.alien.tooltip", environment.alien(), environment::setAlien),
            soundToggle("options.ambience_garden.mansion", "options.ambience_garden.mansion.tooltip", environment.mansion(), environment::setMansion),
            soundToggle("options.ambience_garden.mineshaft", "options.ambience_garden.mineshaft.tooltip", environment.mineshaft(), environment::setMineshaft),
            soundToggle("options.ambience_garden.geode", "options.ambience_garden.geode.tooltip", environment.geode(), environment::setGeode),
            soundToggle("options.ambience_garden.gravel", "options.ambience_garden.gravel.tooltip", environment.gravel(), environment::setGravel),
            soundToggle("options.ambience_garden.deepslate", "options.ambience_garden.deepslate.tooltip", environment.deepslate(), environment::setDeepslate),
            soundToggle("options.ambience_garden.underground_water", "options.ambience_garden.underground_water.tooltip", environment.undergroundWater(), environment::setUndergroundWater),
            soundToggle("options.ambience_garden.cave_drone", "options.ambience_garden.cave_drone.tooltip", environment.caveDrone(), environment::setCaveDrone),
            soundToggle("options.ambience_garden.cave_depth", "options.ambience_garden.cave_depth.tooltip", environment.caveDepth(), environment::setCaveDepth)
        );

        list.addHeader(Component.translatable("options.ambience_garden.section.caves"));
        list.addBig(intSlider(
            "options.ambience_garden.cave_drone_cutoff",
            "options.ambience_garden.cave_drone_cutoff.tooltip",
            -64,
            320,
            environment.caveDroneCutoff(),
            environment::setCaveDroneCutoff
        ));
        list.addBig(intSlider(
            "options.ambience_garden.cave_light",
            "options.ambience_garden.cave_light.tooltip",
            0,
            15,
            environment.caveLightLevel(),
            environment::setCaveLightLevel
        ));

        list.addHeader(Component.translatable("options.ambience_garden.section.cave_dimensions"));
        addDimensionToggles(environment.caveDimensions(), environment::setCaveDimensionEnabled);
        LOG.info("Built settings screen with " + list.children().size() + " rows");
    }

    @Override
    protected void addFooter() {
        var row = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        row.addChild(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).width(150).build());
        row.addChild(Button.builder(Component.translatable("gui.ambience_garden.settings.defaults"), button -> this.resetDefaults()).width(150).build());
    }

    @Override
    public void onClose() {
        if (this.list != null) {
            this.list.applyUnsavedChanges();
        }
        LiveSettings.save();
        this.minecraft.setScreen(this.lastScreen);
    }

    private void resetDefaults() {
        Settings.feature().resetToDefaults();
        Biomes.feature().resetToDefaults();
        Environment.feature().resetToDefaults();
        LiveSettings.save();
        LiveSettings.reloadSounds();
        this.minecraft.setScreen(new AmbienceGardenSettingsScreen(this.lastScreen));
    }

    private void addDimensionToggles(List<String> selected, DimensionSetter setter) {
        var ids = new ArrayList<>(VANILLA_DIMENSIONS);
        for (String id : selected) {
            if (!ids.contains(id)) ids.add(id);
        }
        var widgets = new ArrayList<AbstractWidget>();
        for (String id : ids) {
            widgets.add(dimensionButton(id, selected.contains(id), setter));
        }
        this.list.addSmall(widgets);
    }

    private CycleButton<Boolean> dimensionButton(String id, boolean selected, DimensionSetter setter) {
        return CycleButton.onOffBuilder(selected)
            .withTooltip(value -> Tooltip.create(Component.literal(id)))
            .create(dimensionName(id), (button, value) -> {
                setter.set(id, value);
                LiveSettings.save();
            });
    }

    private static Component dimensionName(String id) {
        return switch (id) {
            case "minecraft:overworld" -> Component.translatable("options.ambience_garden.dimension.overworld");
            case "minecraft:the_nether" -> Component.translatable("options.ambience_garden.dimension.nether");
            case "minecraft:the_end" -> Component.translatable("options.ambience_garden.dimension.the_end");
            default -> Component.literal(id);
        };
    }

    private OptionInstance<Boolean> featureToggle(String key, String tooltip, boolean current, Consumer<Boolean> setter) {
        return OptionInstance.createBoolean(
            key,
            OptionInstance.cachedConstantTooltip(Component.translatable(tooltip)),
            current,
            value -> {
                setter.accept(value);
                LiveSettings.reloadSounds();
                LiveSettings.save();
            }
        );
    }

    private OptionInstance<Boolean> soundToggle(String key, String tooltip, boolean current, Consumer<Boolean> setter) {
        return OptionInstance.createBoolean(
            key,
            OptionInstance.cachedConstantTooltip(Component.translatable(tooltip)),
            current,
            value -> {
                setter.accept(value);
                LiveSettings.reloadSounds();
                LiveSettings.save();
            }
        );
    }

    private CycleButton<String> audioChannelButton(Settings settings) {
        var current = settings.audioChannel();
        if (!AUDIO_CHANNELS.contains(current)) current = "ambient";
        return CycleButton.<String>builder(id -> Component.translatable("soundCategory." + id), current)
            .withValues(AUDIO_CHANNELS)
            .withTooltip(value -> Tooltip.create(Component.translatable("options.ambience_garden.audio_channel.tooltip")))
            .create(Component.translatable("options.ambience_garden.audio_channel"), (button, value) -> {
                settings.setAudioChannel(value);
                LiveSettings.reloadSounds();
                LiveSettings.save();
            });
    }

    private static OptionInstance<Double> volumeSlider(String key, String tooltip, double current, Consumer<Double> setter) {
        double value = Math.clamp(current, 0.0, 1.0);
        return new OptionInstance<>(
            key,
            OptionInstance.cachedConstantTooltip(Component.translatable(tooltip)),
            (caption, sliderValue) -> Options.genericValueLabel(caption, Component.literal(Math.round(sliderValue * 100.0) + "%")),
            OptionInstance.UnitDouble.INSTANCE,
            value,
            sliderValue -> setter.accept(sliderValue)
        );
    }

    private static OptionInstance<Integer> intSlider(String key, String tooltip, int min, int max, int current, Consumer<Integer> setter) {
        int low = Math.min(min, current);
        int high = Math.max(max, current);
        return new OptionInstance<>(
            key,
            OptionInstance.cachedConstantTooltip(Component.translatable(tooltip)),
            (caption, value) -> Options.genericValueLabel(caption, value),
            new OptionInstance.IntRange(low, high),
            current,
            setter
        );
    }

    @FunctionalInterface
    private interface DimensionSetter {
        void set(String id, boolean enabled);
    }
}
