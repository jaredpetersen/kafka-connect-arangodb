package io.github.jaredpetersen.kafkaconnectarangodb.source.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.types.Password;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ArangoDbSourceConfig extends AbstractConfig {
  public static final String CONNECTION_URL = "connection.url";
  private static final String CONNECTION_URL_DOC = "Connection url. Can be a domain that returns multiple DNS A records pointing to database servers for cluster support OR point to a single database server.";

  public static final String CONNECTION_JWT = "connection.jwt";
  private static final String CONNECTION_JWT_DOC = "Connection JSON Web Token. Must be superuser in order to tail the Write-Ahead Log.";

  public static final String DB_NAME = "db.name";
  private static final String DB_NAME_DOC = "Database name.";

  public static final ConfigDef CONFIG_DEF = new ConfigDef()
      .define(CONNECTION_URL, Type.STRING, Importance.HIGH, CONNECTION_URL_DOC)
      .define(CONNECTION_JWT, Type.PASSWORD, Importance.HIGH, CONNECTION_JWT_DOC)
      .define(DB_NAME, Type.STRING, Importance.HIGH, DB_NAME_DOC);

  /**
   * Configuration for ArangoDB Sink.
   * @param originals configurations.
   */
  public ArangoDbSourceConfig(final Map<?, ?> originals) {
    super(CONFIG_DEF, originals, true);
  }

  public String getConnectionUrl() {
    return getString(CONNECTION_URL);
  }

  public Password getConnectionJwt() {
    return getPassword(CONNECTION_JWT);
  }

  public String getDatabaseName() {
    return getString(DB_NAME);
  }
}
