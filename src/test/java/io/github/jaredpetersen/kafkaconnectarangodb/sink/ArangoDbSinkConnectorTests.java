package io.github.jaredpetersen.kafkaconnectarangodb.sink;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.jaredpetersen.kafkaconnectarangodb.sink.config.ArangoDbSinkConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.junit.jupiter.api.Test;

public class ArangoDbSinkConnectorTests {
  @Test
  public void versionReturnsVersion() {
    final SinkConnector connector = new ArangoDbSinkConnector();
    assertEquals("1.0.0", connector.version());
  }

  @Test
  public void taskClassReturnsTaskClass() {
    final SinkConnector task = new ArangoDbSinkConnector();
    final Class<? extends Task> taskClass = task.taskClass();

    assertEquals(ArangoDbSinkTask.class, taskClass);
  }

  @Test
  public void taskConfigsReturnsListOfConfigs() {
    final Map<String, String> propsStub = new HashMap<>();
    propsStub.put("asdf", "jkl;");
    propsStub.put("qwer", "uiop");

    final SinkConnector task = new ArangoDbSinkConnector();
    task.start(propsStub);

    final List<Map<String, String>> taskConfigs = task.taskConfigs(9);

    assertEquals(9, taskConfigs.size());

    for (Map<String, String> taskConfig : taskConfigs) {
      assertEquals(propsStub, taskConfig);
    }
  }

  @Test
  public void stopDoesNothing() {
    final SinkConnector task = new ArangoDbSinkConnector();
    task.stop();
  }

  @Test
  public void configFlowReturnsConfig() {
    final SinkConnector task = new ArangoDbSinkConnector();
    final ConfigDef configDef = task.config();

    assertEquals(ArangoDbSinkConfig.CONFIG_DEF, configDef);
  }
}
