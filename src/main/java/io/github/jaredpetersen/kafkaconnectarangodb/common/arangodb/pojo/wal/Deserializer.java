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
      case CREATE_COLLECTION:
        entry = mapper.readValue(node.toString(), CreateCollection.class);
        break;
      case DROP_COLLECTION:
        entry = mapper.readValue(node.toString(), DropCollection.class);
        break;
      case RENAME_COLLECTION:
        entry = mapper.readValue(node.toString(), RenameCollection.class);
        break;
      case CHANGE_COLLECTION:
        entry = mapper.readValue(node.toString(), ChangeCollection.class);
        break;
      case TRUNCATE_COLLECTION:
        entry = mapper.readValue(node.toString(), TruncateCollection.class);
        break;
      // TODO Create index
      // TODO Drop index
      // TODO Create view
      // TODO Drop view
      // TODO Change view
      case START_TRANSACTION:
        entry = mapper.readValue(node.toString(), StartTransaction.class);
        break;
      case COMMIT_TRANSACTION:
        entry = mapper.readValue(node.toString(), CommitTransaction.class);
        break;
      case ABORT_TRANSACTION:
        entry = mapper.readValue(node.toString(), AbortTransaction.class);
        break;
      case REPSERT_DOCUMENT:
        entry = mapper.readValue(node.toString(), RepsertDocument.class);
        break;
      case REMOVE_DOCUMENT:
        entry = mapper.readValue(node.toString(), RemoveDocument.class);
        break;
      default:
        entry = null;
    }

    return entry;
  }
}
