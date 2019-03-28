package io.github.jaredpetersen.kafkaconnectarangodb.sink;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.kafka.connect.sink.SinkTask;
import org.junit.jupiter.api.Test;

public class ArangoDbSinkTaskTests {
  @Test
  public void versionReturnsVersion() {
    final SinkTask task = new ArangoDbSinkTask();
    assertEquals("1.0.4", task.version());
  }

  @Test
  public void stopDoesNothing() {
    final SinkTask task = new ArangoDbSinkTask();
    task.stop();
  }
}
