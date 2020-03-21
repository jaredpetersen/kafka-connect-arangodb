package io.github.jaredpetersen.kafkaconnectarangodb.sink.transforms;

import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.transforms.Transformation;

/**
 * Single Message Transformation that takes CDC records and converts them to use the Simple structure for writing.
 */
public class Cdc<R extends ConnectRecord<R>> implements Transformation<R> {
  private final ConfigDef config = new ConfigDef();

  @Override
  public final void configure(Map<String, ?> configs) {
    // Do nothing
  }

  @Override
  @SuppressWarnings("unchecked")
  public final R apply(R record) {
    // Skip tombstone records
    if (record.value() == null) {
      return null;
    }

    // Remove the "before" section of the record value and keep only the "after"
    final Schema valueSchema;
    final Object value;

    if (record.valueSchema() == null) {
      // Schemaless
      final Map<String, Object> preTransformValue = (Map<String, Object>) record.value();

      valueSchema = null;
      value = preTransformValue.get("after");
    } else {
      // Schemaful
      final Struct preTransformValue = (Struct) record.value();

      valueSchema = record.valueSchema().field("after").schema();
      value = preTransformValue.get("after");
    }

    // Return a new record with the transformed value
    return record.newRecord(
      record.topic(),
      record.kafkaPartition(),
      record.keySchema(),
      record.key(),
      valueSchema,
      value,
      record.timestamp());
  }

  @Override
  public final ConfigDef config() {
    return this.config;
  }

  @Override
  public final void close() {
    // Do nothing
  }
}
