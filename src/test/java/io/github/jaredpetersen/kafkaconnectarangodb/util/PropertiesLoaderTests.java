package io.github.jaredpetersen.kafkaconnectarangodb.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;
import org.junit.jupiter.api.Test;

public class PropertiesLoaderTests {
  @Test
  public void constructorDoesNothing() {
    new PropertiesLoader();
  }

  @Test
  public void loadLoadsProperties() {
    final Properties properties = PropertiesLoader.load();

    assertEquals("1.0.3", properties.get("version"));
  }
}
