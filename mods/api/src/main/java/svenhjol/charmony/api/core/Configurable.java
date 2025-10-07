package svenhjol.charmony.api.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("unused")
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configurable {
    String name() default "";

    String description() default "";

    boolean requireRestart() default true;
}
