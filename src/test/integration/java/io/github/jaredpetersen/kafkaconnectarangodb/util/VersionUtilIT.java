package io.github.jaredpetersen.kafkaconnectarangodb.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import org.junit.jupiter.api.Test;

public class VersionUtilIT {
  @Test
  public void constructorDoesNothing() {
    new VersionUtil();
  }

  @Test
  public void getVersionReturnsVersion() {
    final String version = VersionUtil.getVersion();
    assertTrue(version.matches("[0-9]+\\.[0-9]+\\.[0-9]+"));
  }
}
