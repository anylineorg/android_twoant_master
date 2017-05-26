package net.twoant.master.common_utils.dz_photoview;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class LogManager {

    private static Logger logger = new LoggerDefault();

    public static void setLogger(Logger newLogger) {
        logger = newLogger;
    }

    public static Logger getLogger() {
        return logger;
    }

}
