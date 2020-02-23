package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations.*;

import java.io.IOException;

class Deserializer extends JsonDeserializer<WalEntry> {
  private static final int START_TRANSACTION_CODE = 2200;
  private static final int COMMIT_TRANSACTION_CODE = 2201;
  private static final int ABORT_TRANSACTION_CODE = 2202;
  private static final int REPSERT_DOCUMENT_CODE = 2300;
  private static final int REMOVE_DOCUMENT_CODE = 2302;

  @Override
  public WalEntry deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
    final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
    final int type = node.get("type").intValue();

    final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

    WalEntry entry;

    switch(type) {
      case START_TRANSACTION_CODE:
        entry = mapper.readValue(node.toString(), StartTransaction.class);
        break;
      case COMMIT_TRANSACTION_CODE:
        entry = mapper.readValue(node.toString(), CommitTransaction.class);
        break;
      case ABORT_TRANSACTION_CODE:
        entry = mapper.readValue(node.toString(), AbortTransaction.class);
        break;
      case REPSERT_DOCUMENT_CODE:
        entry = mapper.readValue(node.toString(), RepsertDocument.class);
        break;
      case REMOVE_DOCUMENT_CODE:
        entry = mapper.readValue(node.toString(), RemoveDocument.class);
        break;
      default:
        // Default
        entry = mapper.readValue(node.toString(), Operation.class);
    }

    return entry;
  }
}
