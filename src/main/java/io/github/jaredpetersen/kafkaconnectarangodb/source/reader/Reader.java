package io.github.jaredpetersen.kafkaconnectarangodb.source.reader;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.ArangoDb;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations.RemoveDocument;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations.RepsertDocument;
import io.github.jaredpetersen.kafkaconnectarangodb.sink.writer.ArangoRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Reader {
  private static final Logger LOG = LoggerFactory.getLogger(Reader.class);

  private final ArangoDb arangoDb;
  private Long lastTick;
  private JsonConverter jsonConverter;

  public Reader(ArangoDb arangoDb) {
    this.arangoDb = arangoDb;
    this.lastTick = null;
    this.jsonConverter = new JsonConverter();
  }

  public List<SourceRecord> read() {
    List<WalEntry> walEntries;

    try {
      walEntries = this.arangoDb.tailWal(lastTick);
      LOG.info("result: {}", walEntries);
    }
    catch (IOException exception) {
      LOG.error("failed to tail WAL of ", exception);
      // TODO some better error handling
      walEntries = Collections.emptyList();
    }

    this.lastTick = Long.parseLong(walEntries.get(walEntries.size() - 1).getTick());

    return walEntries.stream()
        .map(this::convert)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private SourceRecord convert(WalEntry walEntry) {
    final SourceRecord record;

    if (walEntry instanceof RemoveDocument) {
      record = convert((RemoveDocument) walEntry);
    }
    else if (walEntry instanceof RepsertDocument) {
      record = convert((RepsertDocument) walEntry);
    }
    else {
      record = null;
    }

    return record;
  }

  private SourceRecord convert(RepsertDocument repsertDocument) {
    Map<String, String> sourcePartition = new HashMap<>();
    sourcePartition.put("db", repsertDocument.getDb());
    sourcePartition.put("cuid", repsertDocument.getCuid());
    sourcePartition.put("host", null); // TODO get from arangodb

    Map<String, String> sourceOffset = new HashMap<>();
    sourceOffset.put("tick", repsertDocument.getTick());

    String topic = repsertDocument.getCuid(); // TODO convert to collection name

    Schema keySchema = SchemaBuilder.struct()
        .name("key").version(1).doc("ArangoDb record identifier")
        .field("_key", Schema.STRING_SCHEMA)
        .field("_rev", Schema.STRING_SCHEMA)
        .build();

    Struct key = new Struct(keySchema);
    key.put("_key", repsertDocument.getData().get("_key").textValue());
    key.put("_rev", repsertDocument.getData().get("_rev").textValue());

    ObjectNode strippedValue = repsertDocument.getData().deepCopy();
    strippedValue.remove("_key");
    strippedValue.remove("_rev");
    strippedValue.remove("_id");

    // Automatically generate schema
    Schema valueSchema = jsonConverter.asConnectSchema(strippedValue);

    return new SourceRecord(sourcePartition, sourceOffset, topic, keySchema, key, valueSchema, strippedValue);
  }

  private SourceRecord convert(RemoveDocument removeDocument) {
    Map<String, String> sourcePartition = new HashMap<>();
    sourcePartition.put("db", removeDocument.getDb());
    sourcePartition.put("cuid", removeDocument.getCuid());
    sourcePartition.put("host", null); // TODO get from arangodb

    Map<String, String> sourceOffset = new HashMap<>();
    sourceOffset.put("tick", removeDocument.getTick());

    String topic = removeDocument.getCuid(); // TODO convert to collection name

    Schema keySchema = SchemaBuilder.struct()
        .name("key").version(1).doc("ArangoDb record identifier")
        .field("_key", Schema.STRING_SCHEMA)
        .field("_rev", Schema.STRING_SCHEMA)
        .build();

    Struct key = new Struct(keySchema);
    key.put("_key", removeDocument.getData().get("_key").textValue());
    key.put("_rev", removeDocument.getData().get("_rev").textValue());

    return new SourceRecord(sourcePartition, sourceOffset, topic, keySchema, key, null, null);
  }
}
