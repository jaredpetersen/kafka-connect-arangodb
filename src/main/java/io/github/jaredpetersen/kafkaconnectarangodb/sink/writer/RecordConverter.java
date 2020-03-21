package io.github.jaredpetersen.kafkaconnectarangodb.sink.writer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.sink.SinkRecord;

/**
 * Convert Kafka Connect records to ArangoDB records.
 */
public class RecordConverter {
  private final JsonConverter jsonConverter;
  private final JsonDeserializer jsonDeserializer;
  private final ObjectMapper objectMapper;

  /**
   * Construct a new RecordConverter.
   * @param jsonConverter Utility for serializing SinkRecords
   * @param jsonDeserializer Utility for deserializing serialized SinkRecords to JSON
   * @param objectMapper Utility for writing JSON to a string
   */
  public RecordConverter(final JsonConverter jsonConverter, final JsonDeserializer jsonDeserializer, final ObjectMapper objectMapper) {
    this.jsonConverter = jsonConverter;
    this.jsonDeserializer = jsonDeserializer;
    this.objectMapper = objectMapper;
  }

  /**
   * Convert SinkRecord to an ArangoRecord.
   * @param record Record to convert
   * @return ArangoRecord equivalent of the SinkRecord
   */
  public final ArangoRecord convert(final SinkRecord record) {
    return new ArangoRecord(
      this.getCollection(record),
      this.getKey(record),
      this.getValue(record));
  }

  /**
   * Get the ArangoDB collection name from the SinkRecord.
   * Collection name is always the last item in the SinkRecord topic, split by a period.
   * @param record Record to get the collection name from
   * @return ArangoDB collection name
   */
  private String getCollection(final SinkRecord record) {
    final String topic = record.topic();
    return topic.substring(topic.lastIndexOf(".") + 1);
  }

  /**
   * Get the ArangoDB document key from the SinkRecord.
   * @param record Record to get the document key from
   * @return ArangoDB document key
   */
  @SuppressWarnings("unchecked")
  private String getKey(final SinkRecord record) {
    final String key;

    if (record.keySchema() == null) {
      // Schemaless
      final Map<String, Object> keyStruct = (Map<String, Object>) record.key();
      final String keyField = keyStruct.keySet().iterator().next();

      key = keyStruct.get(keyField).toString();
    } else {
      // Schemaful
      final Struct keyStruct = (Struct) record.key();
      final Field keyField = record.keySchema().fields().get(0);

      key = keyStruct.get(keyField).toString();
    }

    return key;
  }

  /**
   * Get the ArangoDB document value as stringified JSON from the SinkRecord.
   * @param record Record to get the document value from
   * @return ArangoDB document value in stringified JSON
   */
  @SuppressWarnings("unchecked")
  private String getValue(final SinkRecord record) {
    // Tombstone records don't need to be converted
    if (record.value() == null) {
      return null;
    }

    // Get the name and value of the key field so that we can rename it as "_key" later inside of the record value
    final String keyFieldName;
    final String keyValue;

    if (record.keySchema() == null) {
      // Schemaless
      final Map<String, Object> keyStruct = (Map<String, Object>) record.key();
      keyFieldName = keyStruct.keySet().iterator().next();
      keyValue = keyStruct.get(keyFieldName).toString();
    } else {
      // Schemaful
      final Struct keyStruct = (Struct) record.key();
      keyFieldName = record.keySchema().fields().get(0).name();
      keyValue = keyStruct.get(keyFieldName).toString();
    }

    // Convert the record value to JSON
    final byte[] serializedRecord = jsonConverter.fromConnectData(
      record.topic(),
      record.valueSchema(),
      record.value());
    final JsonNode valueJson = jsonDeserializer.deserialize(record.topic(), serializedRecord);

    // Has to be an object, otherwise we can't write the record to the database
    if (!valueJson.isObject()) {
      throw new IllegalArgumentException("record value is not a single object/document");
    }

    // Include the key in an ArangoDB-format
    final ObjectNode valueJsonObject = (ObjectNode) valueJson;
    valueJsonObject.put("_key", keyValue);
    valueJsonObject.remove(keyFieldName);

    // Return the stringified JSON
    try {
      return this.objectMapper.writeValueAsString(valueJsonObject);
    } catch (JsonProcessingException exception) {
      throw new IllegalArgumentException("record value cannot be serialized to JSON");
    }
  }
}
