package io.github.jaredpetersen.kafkaconnectarangodb.source;

import io.github.jaredpetersen.kafkaconnectarangodb.source.config.ArangoDbSourceConfig;
import io.github.jaredpetersen.kafkaconnectarangodb.common.util.VersionUtil;
import io.github.jaredpetersen.kafkaconnectarangodb.source.config.ArangoDbSourceTaskConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Entry point for Kafka Connect ArangoDB Source.
 */
public class ArangoDbSourceConnector extends SourceConnector {
  private ArangoDbSourceConfig config;
  private DatabaseHostMonitorThread databaseHostMonitor = null;

  private static final Logger LOG = LoggerFactory.getLogger(ArangoDbSourceConnector.class);
  private static final long POLL_INTERVAL = 30_000;

  @Override
  public void start(Map<String, String> props) {
    this.config = new ArangoDbSourceConfig(props);

    // Start a database monitoring thread to keep track of IP addresses
    // This allows us to be flexible as the database scales up / down
    this.databaseHostMonitor = new DatabaseHostMonitorThread(this.context, POLL_INTERVAL, this.config.getConnectionUrl());
    this.databaseHostMonitor.start();
  }

  @Override
  public Class<? extends Task> taskClass() {
    return ArangoDbSourceTask.class;
  }

  @Override
  public List<Map<String, String>> taskConfigs(final int maxTasks) {
    // Get db server addresses from the monitoring thread
    final List<String> databaseAddresses = new ArrayList<>(this.databaseHostMonitor.getDatabaseAddresses());

    // Can only use one task because we need to combine the data from ArangoDB before sending it off
    final Map<String, String> taskConfig = new HashMap<>();
    taskConfig.put(ArangoDbSourceTaskConfig.CONNECTION_URL, String.join(",", databaseAddresses));
    taskConfig.put(ArangoDbSourceTaskConfig.CONNECTION_JWT, this.config.getConnectionJwt().value());
    taskConfig.put(ArangoDbSourceTaskConfig.DB_NAME, this.config.getDatabaseName());

    final List<Map<String, String>> taskConfigs = new ArrayList<>();
    taskConfigs.add(taskConfig);

    return taskConfigs;
  }

  @Override
  public void stop() {
    this.databaseHostMonitor.shutdown();
    this.databaseHostMonitor = null;
  }

  @Override
  public ConfigDef config() {
    return ArangoDbSourceConfig.CONFIG_DEF;
  }

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }
}
