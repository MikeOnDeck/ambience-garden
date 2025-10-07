package svenhjol.charmony.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public final class Api {
    private static final Logger LOGGER = LogManager.getLogger("CharmonyApi");
    private static final String ENDPOINT = "svenhjol.charmony.core.Api";
    private static int failures = 0;

    public static void registerProvider(Object provider) {
        try {
            var clazz = Class.forName(ENDPOINT);
            var method = clazz.getMethod("registerProvider", Object.class);
            method.invoke(null, provider);
            failures = 0;
        } catch (Exception e) {
            // Don't keep spamming the logs after a certain number of failures.
            if (failures < 5) {
                LOGGER.debug("API call failed: {}", failures);
                failures++;
            }
        }
    }
}
