package io.github.jaredpetersen.kafkaconnectarangodb.source;

import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.ArangoDb;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations.Operation;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations.RepsertDocument;
import io.github.jaredpetersen.kafkaconnectarangodb.common.util.VersionUtil;
import io.github.jaredpetersen.kafkaconnectarangodb.source.config.ArangoDbSourceTaskConfig;
import io.github.jaredpetersen.kafkaconnectarangodb.source.reader.Reader;
import io.github.jaredpetersen.kafkaconnectarangodb.source.reader.SourceRecordComparator;
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
  private final WalEntryRevComparator walEntryRevComparator = new WalEntryRevComparator();
  private final List<WalEntry> walEntries = new ArrayList<>();

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
      final ArangoDb arangoDb = ArangoDb.builder()
          .host(connectionUrl)
          .jwt(config.getConnectionJwt().value())
          .database("_system")
          .build();

      this.readers.add(new Reader(arangoDb));
    }
  }

  @Override
  public List<SourceRecord> poll() throws InterruptedException {
    LOG.info("reading record(s)");

    for (Reader reader : this.readers) {
      walEntries.addAll(reader.read());
    }

    // Sort records by the _rev field, which is a Hybrid Logical Clock under the covers
    walEntries.sort(walEntryRevComparator);

    LOG.info(walEntries.toString());

    // Return records that have been around "long enough", i.e. we're confident that we're
    // not going to have any stragglers come in later to ruin our ordering
    return Collections.emptyList();
  }

  @Override
  public void stop() {
    // TODO save lastTick from all of the readers along with the host
  }
}
