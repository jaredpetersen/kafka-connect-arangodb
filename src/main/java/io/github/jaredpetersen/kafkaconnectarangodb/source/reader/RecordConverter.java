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
  private static final byte[] revDecodeTable = {
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   //   0 - 15
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   //  16 - 31
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, 0,  -1, -1,   //  32 - 47
      54, 55, 56, 57, 58, 59, 60, 61,
      62, 63, -1, -1, -1, -1, -1, -1,   //  48 - 63
      -1, 2,  3,  4,  5,  6,  7,  8,
      9,  10, 11, 12, 13, 14, 15, 16,   //  64 - 79
      17, 18, 19, 20, 21, 22, 23, 24,
      25, 26, 27, -1, -1, -1, -1, 1,    //  80 - 95
      -1, 28, 29, 30, 31, 32, 33, 34,
      35, 36, 37, 38, 39, 40, 41, 42,   //  96 - 111
      43, 44, 45, 46, 47, 48, 49, 50,
      51, 52, 53, -1, -1, -1, -1, -1,   // 112 - 127
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   // 128 - 143
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   // 144 - 159
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   // 160 - 175
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   // 176 - 191
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   // 192 - 207
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   // 208 - 223
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1,   // 224 - 239
      -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1, -1, -1, -1, -1, -1, -1 }; // 240 - 255

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
    final Map<String, String> sourcePartition = createSourcePartition(
        repsertDocument.getDb(),
        repsertDocument.getCuid(),
        null);

    final String rev = repsertDocument.getData().get(REV).asText();
    final Map<String, String> sourceOffset = createSourceOffset(rev);

    final String topic = repsertDocument.getCuid(); // TODO convert to collection name

    final Integer partition = 0;

    final Struct key = new Struct(KEY_SCHEMA);
    key.put(KEY, repsertDocument.getData().get(KEY).textValue());
    key.put(REV, repsertDocument.getData().get(REV).textValue()); // TODO probably don't actually care about this

    final ObjectNode strippedValue = repsertDocument.getData().deepCopy();
    strippedValue.remove(KEY);
    strippedValue.remove(REV);
    strippedValue.remove(ID);

    // Automatically generate schema
    final Schema valueSchema = jsonConverter.asConnectSchema(strippedValue);

    return new SourceRecord(sourcePartition, sourceOffset, topic, partition, KEY_SCHEMA, key, valueSchema, strippedValue);
  }

  private SourceRecord convert(RemoveDocument removeDocument) {
    final Map<String, String> sourcePartition = createSourcePartition(
        removeDocument.getDb(),
        removeDocument.getCuid(),
        null);

    final String rev = removeDocument.getData().get(REV).asText();
    final Map<String, String> sourceOffset = createSourceOffset(rev);

    final String topic = removeDocument.getCuid(); // TODO convert to collection name

    final Integer partition = 0;

    final Struct key = new Struct(KEY_SCHEMA);
    key.put(KEY, removeDocument.getData().get(KEY).textValue());
    key.put(REV, removeDocument.getData().get(REV).textValue());

    final Schema valueSchema = null;

    final Object value = null;

    return new SourceRecord(sourcePartition, sourceOffset, topic, partition, KEY_SCHEMA, key, valueSchema, value);
  }

  private Map<String, String> createSourcePartition(String db, String cuid, String host) {
    final Map<String, String> sourcePartition = new HashMap<>();
    sourcePartition.put("db", db);
    sourcePartition.put("cuid", cuid);
    sourcePartition.put("host", host); // TODO get from arangodb

    return sourcePartition;
  }

  private Map<String, String> createSourceOffset(String rev) {
    final Map<String, String> sourcePartition = new HashMap<>();
    sourcePartition.put("rev", String.valueOf(convertToHybridLogicalClock(rev)));
    // Maybe include tick + server as well, since offset is maybe used to restart again in the case of shutdown?

    return sourcePartition;
  }

  private long convertToHybridLogicalClock(String rev) {
    long r = 0;
    for (char c : rev.toCharArray()) {
      int i = revDecodeTable[c];

      if (i < 0) {
        System.out.println("uh oh");
      }

      r = (r << 6) | i;
    }

    return r;
  }
}
