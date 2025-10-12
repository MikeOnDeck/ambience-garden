package charmony.core.base;

import com.google.common.base.CaseFormat;
import com.mojang.logging.LogUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public final class Log {
    private final Logger log;

    public Log(String id) {
        log = LogUtils.getLogger();
    }

    public Log(String id, String suffix) {
        var name = snakeToUpperCamel(id) + "/" + suffix;
        log = LoggerFactory.getLogger(name);
    }

    public Log(String id, Object object) {
        this(id, object.getClass().getSimpleName());
    }

    public Logger getLogger() {
        return log;
    }

    public void info(String message, Object... args) {
        log.info(message, args);
    }

    public void warn(String message, Object... args) {
        log.warn(message, args);
    }

    public void error(String message, Object... args) {
        log.error(message, args);
    }

    public void debug(String message, Object... args) {
        if (Environment.isDebugMode()) {
            info("[Debug] " + message, args);
        }
    }

    public void warnIfDebug(String message, Object... args) {
        if (Environment.isDebugMode()) {
            warn("[Debug] " + message, args);
        }
    }

    public void dev(String message, Object... args) {
        if (Environment.isDevEnvironment()) {
            info("[Dev] " + message, args);
        }
    }

    public void warnIfDev(String message, Object... args) {
        if (Environment.isDevEnvironment()) {
            warn("[Dev] " + message, args);
        }
    }

    public void die(Throwable e) throws RuntimeException {
        var cause = e.getCause();
        var message = (cause != null ? cause.getMessage() : e.getMessage());
        die(message, e);
    }

    public void die(String message, Throwable e) throws RuntimeException {
        var stacktrace = ExceptionUtils.getStackTrace(e);
        error(stacktrace);
        die(message);
    }

    public void die(String message) throws RuntimeException {
        error(message);
        throw new RuntimeException(message);
    }

    private String snakeToUpperCamel(String string) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, string);
    }
}
