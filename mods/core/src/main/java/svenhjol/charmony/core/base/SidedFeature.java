package svenhjol.charmony.core.base;

import com.google.common.base.CaseFormat;
import net.minecraft.resources.ResourceLocation;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.Charmony;

import java.util.function.BooleanSupplier;

@SuppressWarnings({"unused"})
public abstract class SidedFeature {
    private final Mod mod;
    private final Log log;

    private boolean enabled;

    public SidedFeature(Mod mod) {
        this.mod = mod;
        this.log = new Log(mod.id(), className());
        this.enabled = enabledByDefault();
        this.mod.addCheckStep(this, check());
    }

    public Mod mod() {
        return mod;
    }

    public ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(mod.id(), path);
    }

    public ResourceLocation registryId(String path) {
        return Charmony.id(path);
    }

    /**
     * Returns a pretty-format version of the feature name.
     * For example, "CharmonySettings" becomes "Charmony settings".
     * Use className() for the pascal-case feature name.
     *
     * @return Pretty-format feature name.
     */
    public String name() {
        var name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className());
        var first = name.substring(0, 1);
        return first.toUpperCase() + name.replace("_", " ").substring(1);
    }

    /**
     * Pascal-case feature name.
     *
     * @return Pascal-case feature name.
     */
    public String className() {
        return this.getClass().getSimpleName();
    }

    public String description() {
        return annotation().description();
    }

    public Log log() {
        return log;
    }

    public Config config() {
        return mod().config();
    }

    public boolean enabled() {
        return enabled;
    }

    public void enabled(boolean flag) {
        this.enabled = flag;
    }

    public boolean enabledByDefault() {
        return annotation().enabledByDefault();
    }

    public boolean canBeDisabled() {
        return annotation().canBeDisabled();
    }

    public boolean canBeDisabledInConfig() {
        return annotation().canBeDisabledInConfig();
    }

    /**
     * Runs when the feature is enabled.
     */
    public void run() {
        // no op
    }

    public BooleanSupplier check() {
        return () -> true;
    }

    public Side side() {
        return annotation().side();
    }

    private FeatureDefinition annotation() {
        var annotation = getClass().getAnnotation(FeatureDefinition.class);
        if (annotation == null) {
            throw new RuntimeException("Feature " + getClass() + " is missing annotation");
        }
        return annotation;
    }
}
