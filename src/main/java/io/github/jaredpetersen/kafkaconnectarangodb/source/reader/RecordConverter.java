package io.github.jaredpetersen.kafkaconnectarangodb.source.reader;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations.RemoveDocument;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations.RepsertDocument;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.HashMap;
import java.util.Map;

public class RecordConverter {
  private final JsonConverter jsonConverter;

  private static final String KEY = "_key";
  private static final String REV = "_rev";
  private static final String ID = "_id";
  private static final Schema KEY_SCHEMA = SchemaBuilder.struct()
      .name("key").version(1).doc("ArangoDb record identifier")
      .field(KEY, Schema.STRING_SCHEMA)
      .field(REV, Schema.STRING_SCHEMA)
      .build();

  public RecordConverter() {
    this.jsonConverter = new JsonConverter();
  }

  public SourceRecord convert(WalEntry walEntry) {
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
    final Map<String, String> sourcePartition = createSourceParition(
        repsertDocument.getDb(),
        repsertDocument.getCuid(),
        null);

    Map<String, String> sourceOffset = new HashMap<>();
    sourceOffset.put("tick", repsertDocument.getTick());

    String topic = repsertDocument.getCuid(); // TODO convert to collection name

    Integer partition = 0;

    Struct key = new Struct(KEY_SCHEMA);
    key.put(KEY, repsertDocument.getData().get(KEY).textValue());
    key.put(REV, repsertDocument.getData().get(REV).textValue());

    ObjectNode strippedValue = repsertDocument.getData().deepCopy();
    strippedValue.remove(KEY);
    strippedValue.remove(REV);
    strippedValue.remove(ID);

    // Automatically generate schema
    Schema valueSchema = jsonConverter.asConnectSchema(strippedValue);

    return new SourceRecord(sourcePartition, sourceOffset, topic, partition, KEY_SCHEMA, key, valueSchema, strippedValue);
  }

  private SourceRecord convert(RemoveDocument removeDocument) {
    final Map<String, String> sourcePartition = createSourceParition(
        removeDocument.getDb(),
        removeDocument.getCuid(),
        null);

    final Map<String, String> sourceOffset = new HashMap<>();
    sourceOffset.put("tick", removeDocument.getTick());

    final String topic = removeDocument.getCuid(); // TODO convert to collection name

    final Integer partition = 0;

    final Struct key = new Struct(KEY_SCHEMA);
    key.put(KEY, removeDocument.getData().get(KEY).textValue());
    key.put(REV, removeDocument.getData().get(REV).textValue());

    final Schema valueSchema = null;

    final Object value = null;

    return new SourceRecord(sourcePartition, sourceOffset, topic, partition, KEY_SCHEMA, key, valueSchema, value);
  }

  private Map<String, String> createSourceParition(String db, String cuid, String host) {
    final Map<String, String> sourcePartition = new HashMap<>();
    sourcePartition.put("db", db);
    sourcePartition.put("cuid", cuid);
    sourcePartition.put("host", host); // TODO get from arangodb

    return sourcePartition;
  }
}
