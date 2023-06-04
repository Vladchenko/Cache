package ru.cache.vlad.yanchenko.logging;

import android.support.annotation.NonNull;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import ru.cache.vlad.yanchenko.Repository;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;;

/** Utils to log the caching events. */
public class CacheLoggingUtils {

    private static Logger sLogger;

    /** Get logger to log the events */
    public static Logger getLogger() {
        if  (sLogger == null) {
            sLogger = LogManager.getLogger(CacheLoggingUtils.class);
        }
        return sLogger;
    }
    
    /** Writing a summary about a current caching process to a log file. */
    public static void printSummary(@NonNull Repository repository) {
        sLogger.info("");
        sLogger.info("--- Summary ---------------------------------------");
        sLogger.info("| Cache algorithm : " + repository.getCacheKind());
        sLogger.info("| Pipeline ran for: " + repository.getPipelineRunTimes() + " times");
        sLogger.info("| RAM cache hits  : " + repository.getHitsRAMCache() + " times");
        sLogger.info("| HDD cache hits  : " + repository.getHitsHDDCache() + " times");
        sLogger.info("| RAM cache misses: " + repository.getMissesRAMCache() + " times");
        sLogger.info("| HDD cache misses: " + repository.getMissesHDDCache() + " times");
        sLogger.info("---------------------------------------------------");
    }

    /**
     * Printing the command line arguments
     *
     * @param map of command line parameters
     */
    public static void printArgs(@NonNull Map<String, String> map) {
        if (map.isEmpty()) {
            sLogger.info("No command line arguments present");
        } else {
            for (Map.Entry<String, String> entrySet : map.entrySet()) {
                String key = entrySet.getKey();
                String value = entrySet.getValue();
                sLogger.info(key + "=" + value);
            }
        }
    }

    /** Overriding an implementation of a standard formatter. It's done to remove excessive information that is put to log. */
    static class MyFormatter extends SimpleFormatter {

        private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

        // Changing a string that is going to be put to a log file.
        @Override
        public synchronized String format(@NonNull LogRecord record) {
            return String.format(format,
                    new Date(record.getMillis()),
                    record.getLevel().getLocalizedName(),
                    record.getMessage()
            );
        }
    }
}
