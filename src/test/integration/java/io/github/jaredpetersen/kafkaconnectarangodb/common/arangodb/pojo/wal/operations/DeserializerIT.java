package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeserializerIT {
  private final File walLogFile = new File(getClass().getClassLoader().getResource("wal/wal.json").getFile());

  @Test
  public void deserializesCorrectly() throws IOException {
    final ObjectMapper mapper = new ObjectMapper();
    MappingIterator<WalEntry> iterator = mapper.readerFor(WalEntry.class).readValues(walLogFile);
    List<WalEntry> walEntries = iterator.readAll();

    assertEquals(17, walEntries.size());
    assertEquals(Operation.class, walEntries.get(0).getClass());
    assertEquals(Operation.class, walEntries.get(1).getClass());
    assertEquals(Operation.class, walEntries.get(2).getClass());
    assertEquals(Operation.class, walEntries.get(3).getClass());
    assertEquals(Operation.class, walEntries.get(4).getClass());
    assertEquals(Operation.class, walEntries.get(5).getClass());
    assertEquals(Operation.class, walEntries.get(6).getClass());
    assertEquals(Operation.class, walEntries.get(7).getClass());
    assertEquals(Operation.class, walEntries.get(8).getClass());
    assertEquals(Operation.class, walEntries.get(9).getClass());
    assertEquals(Operation.class, walEntries.get(10).getClass());
    assertEquals(Operation.class, walEntries.get(11).getClass());
    assertEquals(RepsertDocument.class, walEntries.get(15).getClass());
    assertEquals(RemoveDocument.class, walEntries.get(16).getClass());
  }
}
