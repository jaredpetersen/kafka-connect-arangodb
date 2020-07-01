package io.github.jaredpetersen.kafkaconnectarangodb.source.reader;

import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.ArangoDb;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;

import java.util.List;

public class Reader {
  private final ArangoDb arangoDb;
  private Long lastTick;

  public Reader(ArangoDb arangoDb) {
    this.arangoDb = arangoDb;
    this.lastTick = null;
  }

  public List<WalEntry> read() {
    final List<WalEntry> walEntries = this.arangoDb.tailWal(lastTick);

    this.lastTick = Long.parseLong(walEntries.get(walEntries.size() - 1).getTick());

    return walEntries;
  }
}
