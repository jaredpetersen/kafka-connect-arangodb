package io.github.jaredpetersen.kafkaconnectarangodb.source;

import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.ArangoDb;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import io.github.jaredpetersen.kafkaconnectarangodb.common.util.VersionUtil;
import io.github.jaredpetersen.kafkaconnectarangodb.sink.config.ArangoDbSinkConfig;
import io.github.jaredpetersen.kafkaconnectarangodb.source.config.ArangoDbSourceConfig;
import io.github.jaredpetersen.kafkaconnectarangodb.source.config.ArangoDbSourceTaskConfig;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Kafka Connect Task for Kafka Connect ArangoDb Source.
 */
public class ArangoDbSourceTask extends SourceTask {
  private static final Logger LOG = LoggerFactory.getLogger(ArangoDbSourceTask.class);

  private ArangoDbSourceTaskConfig config;

  private ArangoDb arangoDb;
  private Long lastTick = null;

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }

  @Override
  public void start(Map<String, String> props) {
    this.config = new ArangoDbSourceTaskConfig(props);

    // Set up database
    final ArangoDbSourceConfig config = new ArangoDbSourceConfig(props);
    this.arangoDb = new ArangoDb.Builder()
        .host(config.getString(ArangoDbSinkConfig.DB_HOST))
        .port(config.getInt(ArangoDbSinkConfig.ARANGODB_PORT))
        .jwt(config.get.arangoDbJwt.value())
        .build();
  }

  @Override
  public List<SourceRecord> poll() throws InterruptedException {
    LOG.info("reading record(s)");

    try {
      List<WalEntry> walEntries = this.arangoDb.tailWal(lastTick);
      LOG.info("result: {}", walEntries);

      String lastTick = walEntries.get(walEntries.size() - 1).getTick();
      this.lastTick = Long.parseLong(lastTick);
    } catch (IOException exception) {
      LOG.error("failed to tail WAL", exception);
    }

    return null;
  }

  @Override
  public void stop() {

  }
}
