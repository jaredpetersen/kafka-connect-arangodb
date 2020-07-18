package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb;

import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArangoDbIT {
  static {
    Logger.getLogger(MockWebServer.class.getName()).setLevel(Level.OFF);
  }

  @Test
  public void tailWalGetsInitialWal() {
    // given
    final String walLog = "{ \"tick\": \"3651\", \"type\": 2200, \"db\": \"_system\", \"tid\": \"556\" }\n"
        + "{ \"tick\": \"3652\", \"type\": 2201, \"db\": \"_system\", \"tid\": \"556\" }";

    final MockWebServer server = new MockWebServer();
    server.enqueue(new MockResponse()
        .setBody(walLog)
        .setHeader("Content-Type", "application/x-arango-dump; charset=utf-8"));

    final String database = "mydatabase";

    final HttpUrl baseUrl = server.url("");

    final ArangoDb arangoDb = ArangoDb.builder()
        .baseUrl(baseUrl.toString())
        .database(database)
        .jwt("fake")
        .build();

    // when
    final List<WalEntry> walEntries = arangoDb.tailWal(null);

    // then
    assertEquals(2, walEntries.size());
    assertEquals("3651", walEntries.get(0).getTick());
    assertEquals(2200, walEntries.get(0).getType());
    assertEquals("3652", walEntries.get(1).getTick());
    assertEquals(2201, walEntries.get(1).getType());
  }
}
