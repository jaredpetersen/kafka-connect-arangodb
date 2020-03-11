package io.github.jaredpetersen.kafkaconnectarangodb.sink.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

public class ArangoDbSinkConfigTest {
  private Map<String, Object> buildConfigMap() {
    final Map<String, Object> originalsStub = new HashMap<String, Object>();
    originalsStub.put("arangodb.host", "127.0.0.1");
    originalsStub.put("arangodb.port", "8529");
    originalsStub.put("arangodb.user", "root");
    originalsStub.put("arangodb.password", "password");
    originalsStub.put("arangodb.useSsl", true);
    originalsStub.put("arangodb.database.name", "kafka-connect-arangodb");

    return originalsStub;
  }

  @Test
  public void configMissingArangoDbHostThrowsException() {
    final Map<String, Object> originalsStub = buildConfigMap();
    originalsStub.remove("arangodb.host");

    final ConfigException exception = assertThrows(ConfigException.class, () -> new ArangoDbSinkConfig(originalsStub));
    assertEquals("Missing required configuration \"arangodb.host\" which has no default value.", exception.getMessage());
  }

  @Test
  public void configMissingArangoDbPortThrowsException() {
    final Map<String, Object> originalsStub = buildConfigMap();
    originalsStub.remove("arangodb.port");

    final ConfigException exception = assertThrows(ConfigException.class, () -> new ArangoDbSinkConfig(originalsStub));
    assertEquals("Missing required configuration \"arangodb.port\" which has no default value.", exception.getMessage());
  }

  @Test
  public void configMissingArangoDbUserThrowsException() {
    final Map<String, Object> originalsStub = buildConfigMap();
    originalsStub.remove("arangodb.user");

    final ConfigException exception = assertThrows(ConfigException.class, () -> new ArangoDbSinkConfig(originalsStub));
    assertEquals("Missing required configuration \"arangodb.user\" which has no default value.", exception.getMessage());
  }

  @Test
  public void configMissingArangoDbPasswordUsesDefault() {
    final Map<String, Object> originalsStub = buildConfigMap();
    originalsStub.remove("arangodb.password");

    final ArangoDbSinkConfig config = new ArangoDbSinkConfig(originalsStub);

    assertEquals("", config.arangoDbPassword.value());
  }

  @Test
  public void configMissingArangoDbDatabaseNameThrowsException() {
    final Map<String, Object> originalsStub = buildConfigMap();
    originalsStub.remove("arangodb.database.name");

    final ConfigException exception = assertThrows(ConfigException.class, () -> new ArangoDbSinkConfig(originalsStub));
    assertEquals("Missing required configuration \"arangodb.database.name\" which has no default value.", exception.getMessage());
  }

  @Test
  public void configGetArangoDbHostReturnsArangoDbHost() {
    final Map<String, Object> originalsStub = buildConfigMap();
    final ArangoDbSinkConfig config = new ArangoDbSinkConfig(originalsStub);

    assertEquals(originalsStub.get("arangodb.host"), config.arangoDbHost);
  }

  @Test
  public void configGetArangoDbPortReturnsArangoDbPort() {
    final Map<String, Object> originalsStub = buildConfigMap();
    final ArangoDbSinkConfig config = new ArangoDbSinkConfig(originalsStub);

    assertEquals(Integer.parseInt((String) originalsStub.get("arangodb.port")), config.arangoDbPort);
  }

  @Test
  public void configGetArangoDbUserReturnsArangoDbUser() {
    final Map<String, Object> originalsStub = buildConfigMap();
    final ArangoDbSinkConfig config = new ArangoDbSinkConfig(originalsStub);

    assertEquals(originalsStub.get("arangodb.user"), config.arangoDbUser);
  }

  @Test
  public void configGetArangoDbPasswordReturnsArangoDbPassword() {
    final Map<String, Object> originalsStub = buildConfigMap();
    final ArangoDbSinkConfig config = new ArangoDbSinkConfig(originalsStub);

    assertEquals(originalsStub.get("arangodb.password"), config.arangoDbPassword.value());
  }

  @Test
  public void configGetUseSslReturnsUseSsl() {
    final Map<String, Object> originalsStub = buildConfigMap();
    final ArangoDbSinkConfig config = new ArangoDbSinkConfig(originalsStub);

    assertEquals(originalsStub.get("arangodb.useSsl"), config.arangoDbUseSsl);
  }

  @Test
  public void configGetUseSslReturnsDefaultValue() {
    final Map<String, Object> originalsStub = buildConfigMap();
    originalsStub.remove("arangodb.useSsl");
    final ArangoDbSinkConfig config = new ArangoDbSinkConfig(originalsStub);

    assertEquals(false, config.arangoDbUseSsl);
  }

  @Test
  public void configGetArangoDbDatabaseNameReturnsArangoDbDatabaseName() {
    final Map<String, Object> originalsStub = buildConfigMap();
    final ArangoDbSinkConfig config = new ArangoDbSinkConfig(originalsStub);

    assertEquals(originalsStub.get("arangodb.database.name"), config.arangoDbDatabaseName);
  }
}
