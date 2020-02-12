package io.github.jaredpetersen.kafkaconnectarangodb.source;

import io.github.jaredpetersen.kafkaconnectarangodb.source.config.ArangoDbSourceConfig;
import io.github.jaredpetersen.kafkaconnectarangodb.util.VersionUtil;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entry point for Kafka Connect ArangoDB Source.
 */
public class ArangoDbSourceConnector extends SourceConnector {
  private Map<String, String> config;


  @Override
  public void start(Map<String, String> props) {
    this.config = props;

    // TODO monitor headless service and request reconfigure anytime the hosts change
//    this.reconfigure();
  }

  @Override
  public Class<? extends Task> taskClass() {
    return ArangoDbSourceTask.class;
  }

  @Override
  public List<Map<String, String>> taskConfigs(final int maxTasks) {
    List<Map<String, String>> taskConfigs = new ArrayList<>(maxTasks);

    for (int configIndex = 0; configIndex < maxTasks; ++configIndex) {
      taskConfigs.add(this.config);
    }

    return taskConfigs;
  }

  @Override
  public void stop() {
    // Do nothing
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
