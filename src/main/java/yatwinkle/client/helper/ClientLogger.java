package yatwinkle.client.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ClientLogger {

    String LOGGER_NAME = "yatwinkle";

    Logger LOGGER = LoggerFactory.getLogger(LOGGER_NAME);

    default void info(String message) { LOGGER.info(message); }

    default void warn(String message) { LOGGER.warn(message); }

    default void error(String message) { LOGGER.error(message); }

    default void error(String message, Throwable t) { LOGGER.error(message, t); }

    default void debug(String message) { LOGGER.debug(message); }
}
