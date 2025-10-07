package svenhjol.charmony.core.helpers;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class EnumHelper {
    public static <T> T getValueOrDefault(Supplier<T> valueOf, T defaultValue) {
        try {
            return valueOf.get();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
