package io.github.jaredpetersen.kafkaconnectarangodb.source;

import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.ArangoDb;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import io.github.jaredpetersen.kafkaconnectarangodb.common.util.VersionUtil;
import io.github.jaredpetersen.kafkaconnectarangodb.source.config.ArangoDbSourceTaskConfig;
import io.github.jaredpetersen.kafkaconnectarangodb.source.reader.Reader;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Kafka Connect Task for Kafka Connect ArangoDb Source.
 */
public class ArangoDbSourceTask extends SourceTask {
  private static final Logger LOG = LoggerFactory.getLogger(ArangoDbSourceTask.class);

  private List<Reader> readers;
  private Long lastTick = null;

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }

  @Override
  public void start(Map<String, String> props) {
    final ArangoDbSourceTaskConfig config = new ArangoDbSourceTaskConfig(props);

    // Set up readers
    this.readers = new ArrayList<>();

    for (String connectionUrl : config.getConnectionUrls()) {
      ArangoDb arangoDb = new ArangoDb.Builder()
          .host(connectionUrl)
          .jwt(config.getConnectionJwt().value())
          .build();
      Reader reader = new Reader(arangoDb);

      this.readers.add(reader);
    }
  }

  @Override
  public List<SourceRecord> poll() throws InterruptedException {
    LOG.info("reading record(s)");

    // TODO interlace records based on time
    for (Reader reader : this.readers) {
      reader.read();
    }

    // partition is an object that represents where the record came from, e.g.
    // { "db": "database_name", "collection": "table_name"}

    // offset would be like the tick value

    return null;
  }

  @Override
  public void stop() {
    // TODO save lastTick from all of the readers along with the host
  }
}
