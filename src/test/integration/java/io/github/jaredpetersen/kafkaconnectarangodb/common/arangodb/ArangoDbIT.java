package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations.Operation;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ArangoDbIT {
//  @Test
//  public void tailWalGetsInitialWal() throws IOException {
//    Operation operation = new Operation.Builder()
//        .
//
//    List<WalEntry> expectedWalEntries = Arrays.asList(createDatabase, createDatabase);
//
//    MockWebServer server = new MockWebServer();
//    server.enqueue(new MockResponse().setBody(walResponse));
//    server.start();
//
//    ArangoDb arangoDb = new ArangoDb.Builder()
//        .host(server.getHostName(), server.getPort())
//        .build();
//
//    List<WalEntry> walEntries = arangoDb.tailWal();
//
//    assertEquals(expectedWalEntries, walEntries);
//  }
}
