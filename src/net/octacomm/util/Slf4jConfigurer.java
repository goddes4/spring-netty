package net.octacomm.util;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 *
 * @author Taeyoung, Kim
 */
public final class Slf4jConfigurer {
    private Slf4jConfigurer() {
    }

    public static void initLogging(String configLocation) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            // Call context.reset() to clear any previous configuration, e.g. default  
            // configuration. For multi-step configuration, omit calling context.reset(). 
            lc.reset();
            configurator.doConfigure(configLocation);
        } catch (JoranException je) {
            // StatusPrinter will handle this 
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
    }
}
