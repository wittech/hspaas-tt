package com.huashi.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.class);

    public static void debug(String message) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(message);
        }
    }

    public static void info(String message) {
        LOGGER.info(message);
    }
    
    public static void info(String message, Object...arg) {
        LOGGER.info(message, arg);
    }

    public static void error(String message) {
        LOGGER.error(message);
    }

    public static void error(String message, Throwable e) {
        LOGGER.error(message, e);
    }
    
    public static void error(String arg0, Object...arg1) {
        LOGGER.error(arg0, arg1);
    }
}
