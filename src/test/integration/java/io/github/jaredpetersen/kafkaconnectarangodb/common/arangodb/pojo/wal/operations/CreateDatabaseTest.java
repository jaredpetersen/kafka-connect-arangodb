package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateDatabaseTest {
  @Test
  public void deserializesCorrectly() throws JsonProcessingException {
    final String json = "{\n" +
        "  \"tick\": \"2103\",\n" +
        "  \"type\": 1100,\n" +
        "  \"db\": \"test\",\n" +
        "  \"data\": {\n" +
        "    \"database\": 337,\n" +
        "    \"id\": \"337\",\n" +
        "    \"name\": \"test\"\n" +
        "  }\n" +
        "}";

    final ObjectMapper mapper = new ObjectMapper();
    final WalEntry entry = mapper.readValue(json, WalEntry.class);
    final CreateDatabase databaseWalEntry = (CreateDatabase) entry;

    assertEquals("2103", databaseWalEntry.getTick());
    assertEquals(1100, databaseWalEntry.getType());
    assertEquals("test", databaseWalEntry.getDb());
    assertEquals(337, databaseWalEntry.getData().getDatabase());
    assertEquals("337", databaseWalEntry.getData().getId());
    assertEquals("test", databaseWalEntry.getData().getName());
  }

  @Test
  public void serializesCorrectly() throws JsonProcessingException {
    final CreateDatabaseData data = new CreateDatabaseData.Builder()
        .database(337L)
        .id("337")
        .name("test")
        .build();
    final WalEntry entry = new CreateDatabase.Builder()
        .tick("2103")
        .db("test")
        .data(data)
        .build();

    final ObjectMapper mapper = new ObjectMapper();
    final String json = mapper.writeValueAsString(entry);

    final String expectedJson = "{\"tick\":\"2103\",\"type\":1100,\"db\":\"test\",\"data\":{\"database\":337,\"id\":\"337\",\"name\":\"test\"}}";

    assertEquals(expectedJson, json);
  }
}
