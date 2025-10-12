package charmony.core.helpers;

@SuppressWarnings("unused")
public final class TextHelper {
    public static String capitalize(String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }

        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String truncateWithEllipsis(String string, int maxLength) {
        var copy = String.valueOf(string);
        if (copy.length() > maxLength) {
            copy = copy.substring(0, maxLength) + "...";
        }
        return copy;
    }
}
