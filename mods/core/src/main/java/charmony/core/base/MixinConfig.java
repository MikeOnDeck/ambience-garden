package charmony.core.base;

import com.google.common.base.CaseFormat;
import com.moandjiezana.toml.Toml;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import charmony.api.core.FeatureDefinition;
import charmony.api.core.Side;
import charmony.core.Charmony;
import charmony.core.helpers.ConfigHelper;

import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "OptionalUsedAsFieldOrParameterType", "SameParameterValue"})
public abstract class MixinConfig implements IMixinConfigPlugin {
    protected static final Logger LOGGER = LogManager.getLogger("MixinConfig");
    protected static final String FEATURES = "features";
    protected static final List<String> PROTECTED_BASE_NAMES = List.of("accessors", "registry");

    protected final List<String> blacklist = new ArrayList<>();
    protected Optional<Boolean> clientMode = Optional.empty();
    protected Optional<Boolean> debugMode = Optional.empty();
    protected Optional<Boolean> mixinDisableMode = Optional.empty();
    protected String mixinPackage;

    @Override
    public void onLoad(String mixinPackage) {
        // Hold a reference to the mixin package so we can remove it from the fullClassPath later on.
        this.mixinPackage = mixinPackage;
    }

    /**
     * Check conditions as early as possible so that mixins don't get loaded if features are disabled.
     * @param targetClassName Fully qualified class name of the target class
     * @param fullClassPath Fully qualified class name of the mixin
     */
    @Override
    public boolean shouldApplyMixin(String targetClassName, String fullClassPath) {
        var simpleClassPath = fullClassPath.substring(mixinPackage.length() + 1);
        var split = simpleClassPath.split("\\."); // Break apart the class path so we can get the subfolder.
        var baseName = split[0]; // The first bit of the path. This is the subfolder that the mixin class lives in.
        var mixinPrefix = getNiceSideName() + " mixin"; // Logging prefix. "Client mixin", "Common mixin" etc.

        // Allow the subclass to check if this mixin base is permitted.
        if (!allowBaseName(baseName, fullClassPath)) {
            LOGGER.warn("✖ {} {} is not allowed", mixinPrefix, simpleClassPath);
            return false;
        }

        // All mixins other than those with protected base names are disabled when mixin disable mode is active.
        if (isMixinDisableMode() && !PROTECTED_BASE_NAMES.contains(baseName)) {
            LOGGER.warn("✖ Mixin disable mode is active, not loading {}", simpleClassPath);
            return false;
        }

        // Add all runtime blacklist entries to the common blacklist.
        for (var predicate : runtimeBlacklist()) {
            if (predicate.test(baseName)) {
                blacklist.add(simpleClassPath);
            }
        }

        // Check for blacklisted mixin.
        if (blacklist.contains(simpleClassPath)) {
            LOGGER.warn("✖ {} {} is blacklisted", mixinPrefix, simpleClassPath);
            return false;
        }

        // It's valid when it isn't a feature or is feature that is enabled in config.
        // isFeature() checks if the subfolder within the mixins folder matches a feature subfolder.
        var valid = !isFeature(baseName) || enabledInConfig(baseName);
        if (valid) {
            LOGGER.info("✔ Enabled {} {}", mixinPrefix.toLowerCase(), simpleClassPath);
        } else {
            LOGGER.warn("✖ Disabled {} {}", mixinPrefix.toLowerCase(), simpleClassPath);
        }
        return valid;
    }

    /**
     * Let the subclass check if the baseName (the first bit of the path i.e. accessor, feature) is allowed.
     * @param baseName The first bit of the path i.e. accessor, feature
     * @param mixinClassName The mixin classname for reference.
     * @return True if the baseName is allowed.
     */
    protected boolean allowBaseName(String baseName, String mixinClassName) {
        return true;
    }

    /**
     * ID of the mod that extends this base config processor.
     * @return ID of the mod.
     */
    protected abstract String modId();

    /**
     * Classpath to where the "client", "common" and "server" folders are kept.
     * The subclass must define this because it differs per mod.
     * @return Classpath to the client/common/server folders.
     */
    protected abstract String modRoot();

    /**
     * Logical side that this mixin config targets. Multiple mixin configs can exist within the charmony mod
     * and each mixin config should target a specific side.
     * @return Logical side that this mixin config targets.
     */
    protected abstract Side side();

    /**
     * Hook to allow the subclass to specify mixins that should not be included.
     * @return List of predicates to test the currently loading mixin against.
     */
    protected List<Predicate<String>> runtimeBlacklist() {
        return List.of();
    }

    /**
     * Read the "mixin disable mode" value from the charmony config file as early as possible.
     * @return The value of mixin disable mode.
     */
    protected boolean isMixinDisableMode() {
        if (mixinDisableMode.isEmpty()) {
            mixinDisableMode = tryReadFromCoreConfig(Environment.MIXIN_DISABLE_MODE);
        }
        return mixinDisableMode.orElse(false);
    }

    /**
     * Read the "debug mode" value from the charmony config file as early as possible.
     * @return The value of debug mode.
     */
    protected boolean isDebugMode() {
        if (debugMode.isEmpty()) {
            debugMode = tryReadFromCoreConfig(Environment.DEBUG_MODE);
        }
        return debugMode.orElse(false);
    }

    /**
     * Prepares a class path for a given feature baseName.
     * @param baseName Feature baseName in lower snake-case, e.g. `control_panel`
     * @return A classpath for the given baseName.
     */
    protected String getFeatureClassPath(String baseName) {
        var sideName = side().name().toLowerCase(Locale.ROOT);
        var niceName = getNiceFeatureName(baseName);
        return modRoot() + "." + sideName + "." + FEATURES + "." + baseName + "." + niceName;
    }

    /**
     * Formats a lower snake-case feature name into pascal-case.
     * @param baseName Lower snake-case feature name, e.g. `control_panel`
     * @return Pascal-case feature name, e.g. `ControlPanel`
     */
    protected String getNiceFeatureName(String baseName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, baseName);
    }

    /**
     * Formats the name of the side that this mixin config targets.
     * This is typically used to make logging output look good.
     * @return Formatted side name, e.g. "Common"
     */
    protected String getNiceSideName() {
        var sideName = side().name().toLowerCase(Locale.ROOT);
        return sideName.substring(0, 1).toUpperCase(Locale.ROOT) + sideName.substring(1);
    }

    /**
     * Checks if a class exists for a classpath of baseName.
     * @param baseName Feature baseName in lower snake-case, e.g. `control_panel`
     * @return True if a class exists for the given feature.
     */
    protected boolean isFeature(String baseName) {
        try {
            var clazz = Class.forName(getFeatureClassPath(baseName));
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Checks if a feature is enabled in the sided-config file.
     * If not found in the config file or the config file doesn't exist, this method
     * checks the feature annotation for enabledByDefault.
     * @param baseName Feature baseName in lower snake-case, e.g. `control_panel`
     * @return True if the feature is enabled in the config file, or if the file doesn't exist, enabled by default.
     */
    protected boolean enabledInConfig(String baseName) {
        FeatureDefinition feature;

        try {
            var clazz = Class.forName(getFeatureClassPath(baseName));
            feature = clazz.getAnnotation(FeatureDefinition.class);
        } catch (ClassNotFoundException e) {
            return false;
        }

        if (feature == null) {
            LOGGER.error("Feature {} is missing annotation", baseName);
            return false;
        }

        if (!feature.canBeDisabled()) {
            return true;
        }

        var niceName = getNiceFeatureName(baseName);

        // Check config file for all sides.
        Optional<Boolean> configured = Optional.empty();

        for (Side side : Side.values()) {
            var configFile = ConfigHelper.configPath(modId(), side).toFile();

            if (configFile.exists()) {
                // Read the value from the config file.
                var handle = new Toml();
                var toml = handle.read(configFile);
                var enabledPath = niceName + ".Enabled";

                if (toml.contains(enabledPath)) {
                    if (configured.isEmpty() || configured.get()) {
                        configured = Optional.of(toml.getBoolean(enabledPath));
                    }
                }
            }
        }

        return configured.orElseGet(feature::enabledByDefault);
    }

    /**
     * Helper method to read a core key from the mod's config file.
     * @param key Key to read, e.g. "Debug Mode"
     * @return Optional null if the key value is not set, or the boolean value of the key.
     */
    private Optional<Boolean> tryReadFromCoreConfig(String key) {
        var configFile = ConfigHelper.configPath(Charmony.ID, Side.Common).toFile();

        if (configFile.exists()) {
            var handle = new Toml();
            var toml = handle.read(configFile);
            var configKey = "Core.\"" + key + "\"";
            if (toml.contains(configKey)) {
                return Optional.of(toml.getBoolean(configKey));
            }
        }

        return Optional.empty();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // no op
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // no op
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // no op
    }
}
