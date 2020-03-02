package io.github.jaredpetersen.kafkaconnectarangodb.source;

import io.github.jaredpetersen.kafkaconnectarangodb.source.config.ArangoDbSourceConfig;
import io.github.jaredpetersen.kafkaconnectarangodb.common.util.VersionUtil;
import io.github.jaredpetersen.kafkaconnectarangodb.source.config.ArangoDbSourceTaskConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.util.ConnectorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Entry point for Kafka Connect ArangoDB Source.
 */
public class ArangoDbSourceConnector extends SourceConnector {
  private ArangoDbSourceConfig config;
  private DatabaseHostMonitorThread databaseHostMonitor = null;

  private static final Logger LOG = LoggerFactory.getLogger(ArangoDbSourceConnector.class);

  @Override
  public void start(Map<String, String> props) {
    this.config = new ArangoDbSourceConfig(props);

    // Start a database monitoring thread to keep track of IP addresses
    // This allows us to be flexible as the database scales up / down
    long pollInterval = 30000;
    this.databaseHostMonitor = new DatabaseHostMonitorThread(this.context, pollInterval, this.config.getConnectionUrl());
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

    // Split the database addresses into batches that can be distributed amongst the sink tasks
    final int taskCount = Math.min(maxTasks, databaseAddresses.size());
    final List<List<String>> databaseAddressBatches = ConnectorUtils.groupPartitions(databaseAddresses, taskCount);

    List<Map<String, String>> taskConfigs = new ArrayList<>();

    for (int i = 0; i < taskCount; ++i) {
      Map<String, String> taskConfig = new HashMap<>();
      taskConfig.put(ArangoDbSourceTaskConfig.CONNECTION_URL, String.join(",", databaseAddressBatches.get(i)));
      taskConfig.put(ArangoDbSourceTaskConfig.CONNECTION_JWT, this.config.getConnectionJwt().value());
      taskConfig.put(ArangoDbSourceTaskConfig.DB_NAME, String.join(",", this.config.getDatabaseNames()));

      taskConfigs.add(taskConfig);
    }

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
