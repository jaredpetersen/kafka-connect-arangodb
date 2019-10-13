package io.github.jaredpetersen.kafkaconnectarangodb.sink;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.kafka.connect.sink.SinkTask;
import org.junit.jupiter.api.Test;

public class ArangoDbSinkTaskIT {
  @Test
  public void versionReturnsVersion() {
    final SinkTask task = new ArangoDbSinkTask();
    assertTrue(task.version().matches("[0-9]+\\.[0-9]+\\.[0-9]+"));
  }

  @Test
  public void stopDoesNothing() {
    final SinkTask task = new ArangoDbSinkTask();
    task.stop();
  }
}
