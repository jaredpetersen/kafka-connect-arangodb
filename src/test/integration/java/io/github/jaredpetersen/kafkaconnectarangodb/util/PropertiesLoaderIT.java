package io.github.jaredpetersen.kafkaconnectarangodb.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import org.junit.jupiter.api.Test;

public class PropertiesLoaderIT {
  @Test
  public void constructorDoesNothing() {
    new PropertiesLoader();
  }

  @Test
  public void loadLoadsProperties() {
    final Properties properties = PropertiesLoader.load();
    final String version = (String) properties.get("version");
    assertTrue(version.matches("[0-9]+\\.[0-9]+\\.[0-9]+"));
  }
}
