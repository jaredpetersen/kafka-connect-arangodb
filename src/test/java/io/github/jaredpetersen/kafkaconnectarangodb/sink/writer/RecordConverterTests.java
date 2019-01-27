package io.github.jaredpetersen.kafkaconnectarangodb.sink.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.json.JsonConverterConfig;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.Test;

public class RecordConverterTests {
  private final Schema keyStructSchema = SchemaBuilder.struct()
      .name("key").version(1).doc("key schema")
      .field("Id", Schema.INT32_SCHEMA)
      .build();
  private final Schema valueStructSchema = SchemaBuilder.struct()
      .name("value").version(1).doc("value schema")
      .field("Name", Schema.STRING_SCHEMA)
      .field("Age", Schema.INT32_SCHEMA)
      .build();

  @Test
  public void convertSchemafulRecordReturnsArangoRecord() {
    // Set up stub data
    final Struct keyStub = new Struct(this.keyStructSchema)
        .put("Id", 45);
    final Struct valueStub = new Struct(this.valueStructSchema)
        .put("Name", "Henry")
        .put("Age", 2);
    final SinkRecord sinkRecordStub = new SinkRecord(
        "some-prefix.table",
        1,
        keyStub.schema(),
        keyStub,
        valueStub.schema(),
        valueStub,
        0);

    // Set up RecordConverter dependencies
    final JsonConverter jsonConverter = new JsonConverter();
    jsonConverter.configure(
        Collections.singletonMap(JsonConverterConfig.SCHEMAS_ENABLE_CONFIG, "false"),
        false);

    final JsonDeserializer jsonDeserializer = new JsonDeserializer();
    final ObjectMapper objectMapper = new ObjectMapper();

    // Set up expected data
    final ArangoRecord expectedArangoRecord = new ArangoRecord("table", "45", "{\"Name\":\"Henry\",\"Age\":2,\"_key\":\"45\"}");

    // Test system under test
    final RecordConverter recordConverter = new RecordConverter(jsonConverter, jsonDeserializer, objectMapper);
    final ArangoRecord arangoRecord = recordConverter.convert(sinkRecordStub);

    assertEquals(expectedArangoRecord, arangoRecord);
  }

  @Test
  public void convertSchemafulTombstoneRecordReturnsArangoRecord() {
    // Set up stub data
    final Struct keyStub = new Struct(this.keyStructSchema)
        .put("Id", 45);
    final Struct valueStub = null;
    final SinkRecord sinkRecordStub = new SinkRecord(
        "some-prefix.table",
        1,
        this.keyStructSchema,
        keyStub,
        this.valueStructSchema,
        valueStub,
        0);

    // Set up RecordConverter dependencies
    final JsonConverter jsonConverter = new JsonConverter();
    jsonConverter.configure(
        Collections.singletonMap(JsonConverterConfig.SCHEMAS_ENABLE_CONFIG, "false"),
        false);

    final JsonDeserializer jsonDeserializer = new JsonDeserializer();
    final ObjectMapper objectMapper = new ObjectMapper();

    // Set up expected data
    final ArangoRecord expectedArangoRecord = new ArangoRecord("table", "45", null);

    // Test system under test
    final RecordConverter recordConverter = new RecordConverter(jsonConverter, jsonDeserializer, objectMapper);
    final ArangoRecord arangoRecord = recordConverter.convert(sinkRecordStub);

    assertEquals(expectedArangoRecord, arangoRecord);
  }

  @Test
  public void convertSchemafulNonSingleObjectRecordThrowsException() {
    // Set up stub data
    final Struct keyStub = new Struct(this.keyStructSchema)
        .put("Id", 45);
    final List<Struct> valueStub = Arrays.asList(
        new Struct(this.valueStructSchema)
          .put("Name", "Eleanor")
          .put("Age", 3),
        new Struct(this.valueStructSchema)
          .put("Name", "Henry")
          .put("Age", 2));
    final SinkRecord sinkRecordStub = new SinkRecord(
        "some-prefix.table",
        1,
        this.keyStructSchema,
        keyStub,
        SchemaBuilder.array(this.valueStructSchema).build(),
        valueStub,
        0);

    // Set up RecordConverter dependencies
    final JsonConverter jsonConverter = new JsonConverter();
    jsonConverter.configure(
        Collections.singletonMap(JsonConverterConfig.SCHEMAS_ENABLE_CONFIG, "false"),
        false);

    final JsonDeserializer jsonDeserializer = new JsonDeserializer();
    final ObjectMapper objectMapper = new ObjectMapper();

    // Test system under test
    final RecordConverter recordConverter = new RecordConverter(jsonConverter, jsonDeserializer, objectMapper);

    Exception thrownException = assertThrows(IllegalArgumentException.class, () -> recordConverter.convert(sinkRecordStub));
    assertEquals("record value is not a single object/document", thrownException.getMessage());
  }

  @Test
  public void convertSchemalessRecordReturnsArangoRecord() {
    // Set up stub data
    final Map<String, Object> keyStub = new LinkedHashMap<>();
    keyStub.put("Id", 45);

    final Map<String, Object> valueStub = new LinkedHashMap<>();
    valueStub.put("Name", "Henry");
    valueStub.put("Age", 2);

    final SinkRecord sinkRecordStub = new SinkRecord(
        "some-prefix.table",
        1,
        null,
        keyStub,
        null,
        valueStub,
        0);

    // Set up RecordConverter dependencies
    final JsonConverter jsonConverter = new JsonConverter();
    jsonConverter.configure(
        Collections.singletonMap(JsonConverterConfig.SCHEMAS_ENABLE_CONFIG, "false"),
        false);

    final JsonDeserializer jsonDeserializer = new JsonDeserializer();
    final ObjectMapper objectMapper = new ObjectMapper();

    // Set up expected data
    final ArangoRecord expectedArangoRecord = new ArangoRecord("table", "45", "{\"Name\":\"Henry\",\"Age\":2,\"_key\":\"45\"}");

    // Test system under test
    final RecordConverter recordConverter = new RecordConverter(jsonConverter, jsonDeserializer, objectMapper);
    final ArangoRecord arangoRecord = recordConverter.convert(sinkRecordStub);

    assertEquals(expectedArangoRecord, arangoRecord);
  }

  @Test
  public void convertSchemalessTombstoneRecordReturnsArangoRecord() {
    // Set up stub data
    final Map<String, Object> keyStub = new LinkedHashMap<>();
    keyStub.put("Id", 45);

    final Map<String, Object> valueStub = null;

    final SinkRecord sinkRecordStub = new SinkRecord(
        "some-prefix.table",
        1,
        null,
        keyStub,
        null,
        valueStub,
        0);

    // Set up RecordConverter dependencies
    final JsonConverter jsonConverter = new JsonConverter();
    jsonConverter.configure(
        Collections.singletonMap(JsonConverterConfig.SCHEMAS_ENABLE_CONFIG, "false"),
        false);

    final JsonDeserializer jsonDeserializer = new JsonDeserializer();
    final ObjectMapper objectMapper = new ObjectMapper();

    // Set up expected data
    final ArangoRecord expectedArangoRecord = new ArangoRecord("table", "45", null);

    // Test system under test
    final RecordConverter recordConverter = new RecordConverter(jsonConverter, jsonDeserializer, objectMapper);
    final ArangoRecord arangoRecord = recordConverter.convert(sinkRecordStub);

    assertEquals(expectedArangoRecord, arangoRecord);
  }

  @Test
  public void convertSchemalessNonSingleObjectRecordThrowsException() {
    // Set up stub data
    final Map<String, Object> keyStub = new LinkedHashMap<>();
    keyStub.put("Id", 45);

    final Map<String, Object> valueEleanorStub = new LinkedHashMap<>();
    valueEleanorStub.put("Name", "Eleanor");
    valueEleanorStub.put("Age", 3);

    final Map<String, Object> valueHenryStub = new LinkedHashMap<>();
    valueHenryStub.put("Name", "Henry");
    valueHenryStub.put("Age", 2);

    final List<Map<String, Object>> valueStub = Arrays.asList(
        valueEleanorStub,
        valueHenryStub);

    final SinkRecord sinkRecordStub = new SinkRecord(
        "some-prefix.table",
        1,
        null,
        keyStub,
        null,
        valueStub,
        0);

    // Set up RecordConverter dependencies
    final JsonConverter jsonConverter = new JsonConverter();
    jsonConverter.configure(
        Collections.singletonMap(JsonConverterConfig.SCHEMAS_ENABLE_CONFIG, "false"),
        false);

    final JsonDeserializer jsonDeserializer = new JsonDeserializer();
    final ObjectMapper objectMapper = new ObjectMapper();

    // Test system under test
    final RecordConverter recordConverter = new RecordConverter(jsonConverter, jsonDeserializer, objectMapper);

    Exception thrownException = assertThrows(IllegalArgumentException.class, () -> recordConverter.convert(sinkRecordStub));
    assertEquals("record value is not a single object/document", thrownException.getMessage());
  }
}
