package charmony.core.base;

import com.google.common.base.CaseFormat;
import net.minecraft.resources.ResourceLocation;
import charmony.api.core.FeatureDefinition;
import charmony.api.core.ModDefinition;
import charmony.core.common.CommonRegistry;
import charmony.api.core.Side;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public abstract class Mod {
    private static final Map<String, Mod> REGISTERED = new HashMap<>();
    private static final Map<Class<? extends SidedFeature>, SidedFeature> CLASS_SIDED_FEATURES = new HashMap<>();

    private final Log log;
    private final Config config;
    private final Map<String, Feature> features = new HashMap<>();
    private final Map<Side, LinkedList<Class<? extends SidedFeature>>> classes = new LinkedHashMap<>();
    private final Map<Side, Map<SidedFeature, List<Runnable>>> boots = new HashMap<>();
    private final Map<Side, Map<SidedFeature, List<Runnable>>> registers = new HashMap<>();
    private final Map<Side, Map<SidedFeature, List<BooleanSupplier>>> checks = new HashMap<>();

    public Mod() {
        this.config = new Config(this);
        this.log = new Log(id(), "Mod");
        REGISTERED.put(id(), this);
    }

    public static Optional<Mod> get(String id) {
        return Optional.ofNullable(REGISTERED.get(id));
    }

    public static LinkedList<Mod> all() {
        var values = new LinkedList<>(REGISTERED.values());
        values.sort(Comparator.comparing(Mod::name));
        return values;
    }

    /**
     * Resolve a mod feature using a resource location, e.g. charmony:control_panel
     */
    public static Optional<Feature> tryGetFeature(ResourceLocation id) {
        var mod = id.getNamespace();
        var name = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, id.getPath());
        return get(mod).map(m -> m.features.get(name));
    }

    /**
     * Directly get a sided feature's implementation from its class.
     * If the sided feature hasn't been instantiated this method will throw a runtime exception.
     */
    public static <F extends SidedFeature> F getSidedFeature(Class<F> clazz) {
        return tryGetSidedFeature(clazz).orElseThrow(() -> new RuntimeException("Could not resolve feature for class " + clazz));
    }

    /**
     * Get a sided feature's implementation from its class.
     */
    @SuppressWarnings("unchecked")
    public static <F extends SidedFeature> Optional<F> tryGetSidedFeature(Class<F> clazz) {
        F resolved = (F) CLASS_SIDED_FEATURES.get(clazz);
        return Optional.ofNullable(resolved);
    }

    public void run(Side side) {
        var sideName = side.getSerializedName();
        var classes = this.classes.computeIfAbsent(side, l -> new LinkedList<>());
        var classCount = classes.size();

        if (!sides().contains(side)) {
            log.warn("Trying to run side " + sideName + " on a mod that does not support it, skipping");
            return;
        }

        if (classCount == 0) {
            log.info("No " + sideName + " features to set up for " + name() + ", skipping");
            return;
        }

        log.info("Setting up " + classCount + " " + sideName + " feature(s) for " + name());
        classes.sort(Comparator.comparing(c -> c.getAnnotation(FeatureDefinition.class).priority()));

        for (var clazz : classes) {
            SidedFeature sidedFeature;
            try {
                sidedFeature = clazz.getDeclaredConstructor(Mod.class).newInstance(this);
                CLASS_SIDED_FEATURES.put(clazz, sidedFeature);
                features.computeIfAbsent(sidedFeature.className(), c -> new Feature(this)).put(side, sidedFeature);

            } catch (Exception e) {
                String message = "Failed to instantiate feature " + clazz + " for mod " + name() + ": " + e.getMessage();
                if (Environment.isDebugMode()) {
                    throw new RuntimeException(message, e);
                } else {
                    throw new RuntimeException(message);
                }
            }
        }

        var checks = this.checks.computeIfAbsent(side, m -> new HashMap<>());
        var registers = this.registers.computeIfAbsent(side, m -> new HashMap<>());
        var boots = this.boots.computeIfAbsent(side, m -> new HashMap<>());

        log().debug("Configuring " + name() + " " + sideName);
        config.populate();
        config.write();

        log().debug("Checking " + name() + " " + sideName);
        checks.forEach((feature, check) ->
            feature.enabled(feature.enabled() && check.stream().allMatch(BooleanSupplier::getAsBoolean)));

        log().debug("Booting up " + name() + " " + sideName);
        boots.forEach((feature, boot) -> {
            if (feature.enabled()) {
                boot.forEach(Runnable::run);
            }
        });

        log().debug("Registering " + name() + " " + sideName);
        registers.forEach((feature, register)
            -> register.forEach(Runnable::run));

        //noinspection SwitchStatementWithTooFewBranches
        switch (side) {
            case Common -> CommonRegistry.finishModRegistration(this);
        }

        log().info("Running " + sideName + " features for " + name());
        features.forEach((name, feature) -> {
            var featureName = feature.className();
            var sided = feature.get(side).orElse(null);
            if (sided == null) return;

            if (feature.enabled()) {
                log().info("✔ Running " + sideName  + " feature " + featureName);
                sided.run();
            } else {
                log().warn("✖ Not running " + sideName + " feature " + featureName);
                feature.enabled(false);
            }
        });
    }

    public String id() {
        return annotation().id();
    }

    public String description() {
        return annotation().description();
    }

    /**
     * Pretty-format mod name.
     * This is derived from the mod's annotation.
     * @return Pretty-format mod name.
     */
    public String name() {
        return annotation().name();
    }

    public List<Side> sides() {
        return Arrays.asList(annotation().sides());
    }

    /**
     * Pascal-case mod name.
     * @return Pascal-case mod name.
     */
    public String className() {
        return this.getClass().getSimpleName();
    }

    public Log log() {
        return this.log;
    }

    public Config config() {
        return this.config;
    }

    public void addSidedFeatures(List<Class<? extends SidedFeature>> classes) {
        for (var clazz : classes) {
            addSidedFeature(clazz);
        }
    }

    public void addSidedFeature(Class<? extends SidedFeature> clazz) {
        var side = clazz.getAnnotation(FeatureDefinition.class).side();
        classes.computeIfAbsent(side, a -> new LinkedList<>()).add(clazz);
    }

    public void addCheckStep(SidedFeature feature, BooleanSupplier check) {
        checks.computeIfAbsent(feature.side(), m -> new HashMap<>())
            .computeIfAbsent(feature, a -> new ArrayList<>()).add(check);
    }

    public void addBootStep(SidedFeature feature, Runnable step) {
        boots.computeIfAbsent(feature.side(), m -> new HashMap<>())
            .computeIfAbsent(feature, a -> new ArrayList<>()).add(step);
    }

    public void addRegisterStep(SidedFeature feature, Runnable register) {
        registers.computeIfAbsent(feature.side(), m -> new HashMap<>())
            .computeIfAbsent(feature, a -> new ArrayList<>()).add(register);
    }

    public LinkedList<Feature> features() {
        var values = new LinkedList<>(features.values());
        values.sort(Comparator.comparing(Feature::name));
        return values;
    }

    public LinkedList<SidedFeature> sidedFeatures(Side side) {
        return features.values().stream()
            .map(c -> c.get(side))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    private ModDefinition annotation() {
        var annotation = getClass().getAnnotation(ModDefinition.class);
        if (annotation == null) {
            throw new RuntimeException("Mod " + getClass() + " is missing annotation");
        }
        return annotation;
    }
}
