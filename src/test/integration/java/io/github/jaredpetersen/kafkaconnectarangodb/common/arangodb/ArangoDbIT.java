package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb;

import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations.CreateDatabase;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations.CreateDatabaseData;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ArangoDbIT {

  @Test
  public void tailWalGetsInitialWal() throws IOException {
    String walResponse = "{\"tick\":\"2103\",\"type\":1100,\"db\":\"test\",\"data\":{\"database\":337,\"id\":\"337\",\"name\":\"test\"}}\n";
    walResponse = walResponse + "{\"tick\":\"2103\",\"type\":1100,\"db\":\"test\",\"data\":{\"database\":337,\"id\":\"337\",\"name\":\"test\"}}";

    CreateDatabaseData createDatabaseData = new CreateDatabaseData.Builder()
        .database(337L)
        .id("337")
        .name("test")
        .build();
    WalEntry createDatabase = new CreateDatabase.Builder()
        .tick("2103")
        .db("test")
        .data(createDatabaseData)
        .build();

    List<WalEntry> expectedWalEntries = Arrays.asList(createDatabase, createDatabase);

    MockWebServer server = new MockWebServer();
    server.enqueue(new MockResponse().setBody(walResponse));
    server.start();

    ArangoDb arangoDb = new ArangoDb.Builder()
        .host(server.getHostName(), server.getPort())
        .build();

    List<WalEntry> walEntries = arangoDb.tailWal();

    assertEquals(expectedWalEntries, walEntries);
  }
}
