package io.github.jaredpetersen.kafkaconnectarangodb.source;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaredpetersen.kafkaconnectarangodb.source.config.ArangoDbSourceConfig;
import io.github.jaredpetersen.kafkaconnectarangodb.util.VersionUtil;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.json.JsonConverterConfig;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Kafka Connect Task for Kafka Connect ArangoDb Source.
 */
public class ArangoDbSourceTask extends SourceTask {

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }

  @Override
  public void start(Map<String, String> map) {

  }

  @Override
  public List<SourceRecord> poll() throws InterruptedException {
    // TODO hit

    return null;
  }

  @Override
  public void stop() {

  }
}
