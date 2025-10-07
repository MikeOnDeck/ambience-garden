package svenhjol.charmony.api.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation decorates a SidedFeature.
 * A SidedFeature is one "side" of a charmony-feature and gets instantiated when the mod side is set up.
 */
@SuppressWarnings({"unused"})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureDefinition {
    /**
     * Higher priorities will instantiate the feature sooner.
     */
    int priority() default 0;

    /**
     * Description to be shown in the config file and control panel.
     */
    String description() default "";

    /**
     * The "side" (e.g. common) that this sided feature targets.
     * A feature can one or more sides.
     */
    Side side();

    /**
     * If true, this feature can be toggled on or off.
     * If false, this feature is always enabled.
     * Features should have the ability to toggle this state unless the feature is a critical function such as Core.
     */
    boolean canBeDisabled() default true;

    /**
     * If true, this feature is enabled when the mod is first installed.
     * The ability to enable/disable the feature is not affected UNLESS canBeDisabled() is false.
     */
    boolean enabledByDefault() default true;

    /**
     * If true, this sided feature can be enabled or disabled by the side's config file.
     * This has no effect if canBeDisabled is false.
     */
    boolean canBeDisabledInConfig() default true;
}
