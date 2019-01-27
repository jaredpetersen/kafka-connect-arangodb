package io.github.jaredpetersen.kafkaconnectarangodb.util;

import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load configuration from the application's properties file.
 */
public class PropertiesLoader {
  private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesLoader.class);

  /**
   * Load the application's properties file.
   * @return Properties object from the application's properties file.
   */
  public static Properties load() {
    final Properties properties = new Properties();

    try {
      properties.load(PropertiesLoader.class.getClassLoader().getResourceAsStream("kafka-connect-arangodb.properties"));
    } catch (IOException exception) {
      LOGGER.error("failed to load properties", exception);
    }

    return properties;
  }
}
