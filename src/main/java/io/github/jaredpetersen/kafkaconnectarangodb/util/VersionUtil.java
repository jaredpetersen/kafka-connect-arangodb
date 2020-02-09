package io.github.jaredpetersen.kafkaconnectarangodb.util;

import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide version information.
 */
public class VersionUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(VersionUtil.class);
  private static final Properties PROPERTIES = new Properties();

  static {
    try {
      PROPERTIES.load(VersionUtil.class.getClassLoader().getResourceAsStream("kafka-connect-arangodb.properties"));
    } catch (IOException exception) {
      LOGGER.error("failed to load properties", exception);
    }
  }

  /**
   * Get version.
   * @return package version
   */
  public static String getVersion() {
    return PROPERTIES.getProperty("version", "0.0.0");
  }
}
