package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations.CreateDatabase;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations.DropDatabase;

import java.io.IOException;

class Deserializer extends JsonDeserializer<WalEntry> {
  @Override
  public WalEntry deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
    final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
    final Type typeEnum = Type.valueOf(node.get("type").intValue());

    final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

    WalEntry entry;

    switch(typeEnum) {
      case CREATE_DATABASE:
        entry = mapper.readValue(node.toString(), CreateDatabase.class);
        break;
      case DROP_DATABASE:
        entry = mapper.readValue(node.toString(), DropDatabase.class);
        break;
      default:
        entry = null;
    }

    return entry;
  }
}
