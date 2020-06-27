package io.github.jaredpetersen.kafkaconnectarangodb.source;

import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.ArangoDb;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations.Operation;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations.RepsertDocument;
import io.github.jaredpetersen.kafkaconnectarangodb.common.util.VersionUtil;
import io.github.jaredpetersen.kafkaconnectarangodb.source.config.ArangoDbSourceTaskConfig;
import io.github.jaredpetersen.kafkaconnectarangodb.source.reader.Reader;
import io.github.jaredpetersen.kafkaconnectarangodb.source.reader.WalEntryRevComparator;
import io.github.jaredpetersen.kafkaconnectarangodb.source.reader.RecordConverter;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Kafka Connect Task for Kafka Connect ArangoDb Source.
 */
public class ArangoDbSourceTask extends SourceTask {
  private List<Reader> readers;
  private final RecordConverter recordConverter = new RecordConverter();
  private final Comparator<WalEntry> walEntryRevComparator = new WalEntryRevComparator();

  private static final Logger LOG = LoggerFactory.getLogger(ArangoDbSourceTask.class);

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }

  @Override
  public void start(Map<String, String> props) {
    final ArangoDbSourceTaskConfig config = new ArangoDbSourceTaskConfig(props);

    // TODO handle restarts
//    this.context.offsetStorageReader().offset()

    // Set up readers
    this.readers = new ArrayList<>();

    for (String connectionUrl : config.getConnectionUrls()) {
      ArangoDb arangoDb = ArangoDb.builder()
          .host(connectionUrl)
          .jwt(config.getConnectionJwt().value())
          .database("_system");
          // TODO specify database
          .build();
      Reader reader = new Reader(arangoDb);

      this.readers.add(reader);
    }
  }

  @Override
  public List<SourceRecord> poll() throws InterruptedException {
    LOG.info("reading record(s)");

    List<WalEntry> walEntries = new ArrayList<>();

    for (Reader reader : this.readers) {
      walEntries.addAll(reader.read());
    }
    new RepsertDocument.Builder();

    return walEntries.stream()
        .filter(walEntry -> !(walEntry instanceof Operation))
        .sorted(walEntryRevComparator)
        .map(recordConverter::convert)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @Override
  public void stop() {
    // TODO save lastTick from all of the readers along with the host
  }
}
