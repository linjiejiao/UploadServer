package cn.ljj.util;

import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;

public class Logger {
    protected static final int LOG_LEVEL_DEBUG = 1;
    protected static final int LOG_LEVEL_INFO = 2;
    protected static final int LOG_LEVEL_WARNING = 3;
    protected static final int LOG_LEVEL_ERROR = 4;

    private static int currentLogLevel = LOG_LEVEL_DEBUG;
    private static boolean isInitialed = false;
    private static Object lock = new Object();

    public static void init(String initFile) {
        synchronized (lock) {
            if (!isInitialed) {
                PropertyConfigurator.configure(initFile);
                isInitialed = true;
                org.apache.log4j.Logger.getLogger("org.apache.http.wire").setLevel(Level.INFO);
                org.apache.log4j.Logger.getLogger("org.apache.http.impl.conn.PoolingHttpClientConnectionManager")
                        .setLevel(Level.INFO); // avoid sdk debug logs
            }
        }
    }

    public static int d(String tag, String msg) {
        return d(tag, msg, null);
    }

    public static int d(String tag, String msg, Throwable tr) {
        msg = tag + ": " + msg;
        if (tr != null) {
            tr.printStackTrace();
        }
        if (!isInitialed) {
            return 0;
        }
        if (LOG_LEVEL_DEBUG < currentLogLevel) {
            return 0;
        }
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
        logger.debug(msg, tr);
        return 1;
    }

    public static int i(String tag, String msg) {
        return i(tag, msg, null);
    }

    public static int i(String tag, String msg, Throwable tr) {
        msg = tag + ": " + msg;
        if (tr != null) {
            tr.printStackTrace();
        }
        if (!isInitialed) {
            return 0;
        }
        if (LOG_LEVEL_INFO < currentLogLevel) {
            return 0;
        }
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
        logger.debug(msg, tr);
        return 1;
    }

    public static int w(String tag, String msg) {
        return w(tag, msg, null);
    }

    public static int w(String tag, String msg, Throwable tr) {
        msg = tag + ": " + msg;
        if (tr != null) {
            tr.printStackTrace();
        }
        if (!isInitialed) {
            return 0;
        }
        if (LOG_LEVEL_WARNING < currentLogLevel) {
            return 0;
        }
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
        logger.debug(msg, tr);
        return 1;
    }

    public static int e(String tag, String msg) {
        return e(tag, msg, null);
    }

    public static int e(String tag, String msg, Throwable tr) {
        msg = tag + ": " + msg;
        if (tr != null) {
            tr.printStackTrace();
        }
        if (!isInitialed) {
            return 0;
        }
        if (LOG_LEVEL_ERROR < currentLogLevel) {
            return 0;
        }
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
        logger.debug(msg, tr);
        return 1;
    }
}
