package charmony.core.base;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlWriter;
import com.moandjiezana.toml.Toml;
import charmony.api.core.Configurable;
import charmony.api.core.Side;
import charmony.core.helpers.ConfigHelper;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;

public final class Config {
    private final Mod mod;
    private final Log log;
    private final Map<Field, Object> defaultFieldValues = new HashMap<>();
    private final List<Class<? extends SidedFeature>> populatedFeatures = new ArrayList<>();

    public Config(Mod mod) {
        this.mod = mod;
        this.log = new Log(mod.id(), "Config");
    }

    public void populate() {
        var features = mod.features();

        // Initialize a toml object for each side.
        Map<Side, Toml> configs = new HashMap<>();
        for (var side : Side.values()) {
            var toml = new Toml();
            var file = ConfigHelper.configPath(mod.id(), side).toFile();
            if (file.exists()) {
                toml = toml.read(file);
            }
            configs.put(side, toml);
        }

        for (var feature : features) {
            for (var sided : feature.sides()) {
                var toml = configs.get(sided.side());
                var featurePath = sided.className() + ".Enabled";
                if (toml.contains(featurePath) && sided.canBeDisabled()) {
                    sided.enabled(toml.getBoolean(featurePath));
                }

                // Process fields in the feature that have the @Config annotation.
                var fields = new ArrayList<>(Arrays.asList(sided.getClass().getDeclaredFields()));
                for (var field : fields) {
                    try {
                        // Ignore fields that don't have the Config annotation.
                        var annotation = field.getDeclaredAnnotation(Configurable.class);
                        if (annotation == null) continue;

                        // Set the static property as writable so that the config can modify it
                        field.setAccessible(true);
                        var fieldName = annotation.name();

                        if (fieldName.isEmpty()) {
                            fieldName = field.getName();
                        }

                        Object fieldValue = ConfigHelper.getField(field);
                        Object configValue;

                        if (!populatedFeatures.contains(sided.getClass())) {
                            defaultFieldValues.put(field, fieldValue);
                        }

                        // Get the config values that were set in the toml file and apply them to the feature objects.
                        if (toml.contains(sided.className())) {

                            // Get the set of key/value pairs for this feature.
                            var featureKeys = toml.getTable(sided.className());
                            Map<String, Object> mappedKeys = new HashMap<>();

                            // Key names sometimes have quotes, map to remove them.
                            featureKeys.toMap().forEach((k, v) -> mappedKeys.put(k.replace("\"", ""), v));
                            configValue = mappedKeys.get(fieldName);

                            if (configValue != null) {
                                // There's some weirdness with casting, deal with that here.
                                if (fieldValue instanceof Integer && configValue instanceof Double) {
                                    configValue = (int) (double) configValue;
                                }

                                if (fieldValue instanceof Integer && configValue instanceof Long) {
                                    configValue = (int) (long) configValue;
                                }

                                if (fieldValue instanceof Float && configValue instanceof Double) {
                                    configValue = (float) (double) configValue;
                                }

                                // Set the class property.
                                log.debug("In feature " + sided.className() + ": setting `" + fieldName + "` to `" + configValue + "`");
                                ConfigHelper.setField(field, configValue);
                            }
                        }

                    } catch (Exception e) {
                        log.warn("Failed to read config field in feature " + sided.className() + ": " + e.getMessage());
                    }
                }

                if (!populatedFeatures.contains(sided.getClass())) {
                    populatedFeatures.add(sided.getClass());
                }
            }
        }
    }

    public void write() {
        // Blank config is appended and then written out. LinkedHashMap supplier sorts contents alphabetically.
        Map<Side, CommentedConfig> configs = new HashMap<>();
        for (var side : Side.values()) {
            configs.put(side, TomlFormat.newConfig(LinkedHashMap::new));
        }

        for (var feature : mod.features()) {
            for (var sided : feature.sides()) {
                var config = configs.get(sided.side());

                if (sided.canBeDisabled() && sided.canBeDisabledInConfig()) {
                    var field = "Enabled";
                    var description = sided.description();
                    var configName = sided.className() + "." + field;

                    config.setComment(configName, description);
                    config.add(configName, sided.enabled());
                }

                // Get config values and write them into the toml object.
                var fields = new ArrayList<>(Arrays.asList(sided.getClass().getDeclaredFields()));
                for (var field : fields) {
                    try {
                        var annotation = field.getDeclaredAnnotation(Configurable.class);
                        if (annotation == null) continue;

                        var fieldName = annotation.name();
                        var fieldDescription = annotation.description();
                        field.setAccessible(true);
                        Object fieldValue = ConfigHelper.getField(field, sided);

                        // Set the key/value pair. The "." specifies that it is nested
                        var featureConfigName = sided.className() + "." + fieldName;
                        config.setComment(featureConfigName, fieldDescription);
                        config.add(featureConfigName, fieldValue);

                    } catch (Exception e) {
                        log.error("Failed to write config property `" + field.getName() + "` in `" + sided.className() + "`: " + e.getMessage());
                    }
                }
            }
        }

        configs.forEach((side, config) -> {
            if (!config.isEmpty()) {
                var path = ConfigHelper.configPath(mod.id(), side);

                try {
                    // Write out and close the file.
                    var writer = new TomlWriter();
                    var buffer = Files.newBufferedWriter(path);

                    writer.write(config, buffer);
                    buffer.close();
                    log.debug( "Written config to disk");
                } catch (Exception e) {
                    log.error( "Failed to write config: " + e.getMessage());
                }
            }
        });
    }

    public boolean hasConfiguration(Feature feature) {
        for (var sided : feature.sides()) {
            var fields = new ArrayList<>(Arrays.asList(sided.getClass().getDeclaredFields()));
            if (fields.stream().anyMatch(f -> f.getDeclaredAnnotation(Configurable.class) != null)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDefaultValues(Feature feature) {
        for (var sided : feature.sides()) {
            var fields = new ArrayList<>(Arrays.asList(sided.getClass().getDeclaredFields()));
            for (var field : fields) {
                try {
                    var annotation = field.getDeclaredAnnotation(Configurable.class);
                    if (annotation == null) continue;

                    field.setAccessible(true);
                    var val = field.get(null);
                    var opt = defaultValue(field);
                    if (opt.isPresent()) {
                        var defaultVal = opt.get();

                        if (!val.equals(defaultVal)) {
                            return false;
                        }
                    }
                } catch (Exception e) {
                    log.warn("Failed to read config field in feature " + sided.className() + ": " + e.getMessage());
                }
            }
        }

        return true;
    }

    public Optional<Object> defaultValue(Field field) {
        return Optional.ofNullable(defaultFieldValues.get(field));
    }
}
