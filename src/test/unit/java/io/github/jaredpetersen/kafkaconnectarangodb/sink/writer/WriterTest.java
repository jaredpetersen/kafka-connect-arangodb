package io.github.jaredpetersen.kafkaconnectarangodb.sink.writer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDatabase;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

public class WriterTest {
  @Test
  public void writeSingleCollectionRepsertWritesRecords() {
    // Set up input stubs
    final List<ArangoRecord> arangoRecordStubs = Arrays.asList(
        new ArangoRecord("somecollection", "aKey", "ajson"),
        new ArangoRecord("somecollection", "bKey", "bjson"),
        new ArangoRecord("somecollection", "cKey", "cjson"),
        new ArangoRecord("somecollection", "dKey", "djson"),
        new ArangoRecord("somecollection", "eKey", "ejson"));

    // Set up Writer dependencies
    final ArangoCollection collectionMock = mock(ArangoCollection.class);

    final ArangoDatabase databaseMock = mock(ArangoDatabase.class);
    when(databaseMock.collection(any())).thenReturn(collectionMock);

    // Test system under test
    final Writer writer = new Writer(databaseMock);
    writer.write(arangoRecordStubs);

    verify(collectionMock).insertDocuments(
        eq(Arrays.asList("ajson", "bjson", "cjson", "djson", "ejson")),
        argThat(options -> {
          return (options.getOverwrite() == true)
            && (options.getWaitForSync() == true)
            && (options.getSilent() == true);
        }));

    verify(collectionMock, never()).deleteDocuments(any());
  }

  @Test
  public void writeMultipleCollectionRepsertWritesRecords() {
    // Set up input stubs
    final List<ArangoRecord> arangoRecordStubs = Arrays.asList(
        new ArangoRecord("somecollection", "aKey", "ajson"),
        new ArangoRecord("somecollection", "bKey", "bjson"),
        new ArangoRecord("othercollection", "cKey", "cjson"),
        new ArangoRecord("othercollection", "dKey", "djson"),
        new ArangoRecord("elsecollection", "eKey", "ejson"));

    // Set up Writer dependencies
    final ArangoCollection collectionMock = mock(ArangoCollection.class);

    final ArangoDatabase databaseMock = mock(ArangoDatabase.class);
    when(databaseMock.collection(any())).thenReturn(collectionMock);

    // Test system under test
    final Writer writer = new Writer(databaseMock);
    writer.write(arangoRecordStubs);

    InOrder inOrder = Mockito.inOrder(collectionMock);
    inOrder.verify(collectionMock).insertDocuments(
        eq(Arrays.asList("ajson", "bjson")),
        argThat(options -> {
          return (options.getOverwrite() == true)
            && (options.getWaitForSync() == true)
            && (options.getSilent() == true);
        }));
    inOrder.verify(collectionMock).insertDocuments(
        eq(Arrays.asList("cjson", "djson")),
        argThat(options -> {
          return (options.getOverwrite() == true)
            && (options.getWaitForSync() == true)
            && (options.getSilent() == true);
        }));
    inOrder.verify(collectionMock).insertDocuments(
        eq(Arrays.asList("ejson")),
        argThat(options -> {
          return (options.getOverwrite() == true)
            && (options.getWaitForSync() == true)
            && (options.getSilent() == true);
        }));

    verify(collectionMock, never()).deleteDocuments(any());
  }

  @Test
  public void writeSingleCollectionDeleteWritesRecords() {
    // Set up input stubs
    final List<ArangoRecord> arangoRecordStubs = Arrays.asList(
        new ArangoRecord("somecollection", "aKey", null),
        new ArangoRecord("somecollection", "bKey", null),
        new ArangoRecord("somecollection", "cKey", null),
        new ArangoRecord("somecollection", "dKey", null),
        new ArangoRecord("somecollection", "eKey", null));

    // Set up Writer dependencies
    final ArangoCollection collectionMock = mock(ArangoCollection.class);

    final ArangoDatabase databaseMock = mock(ArangoDatabase.class);
    when(databaseMock.collection(any())).thenReturn(collectionMock);

    // Test system under test
    final Writer writer = new Writer(databaseMock);
    writer.write(arangoRecordStubs);

    verify(collectionMock).deleteDocuments(
        eq(Arrays.asList("aKey", "bKey", "cKey", "dKey", "eKey")),
        eq(null),
        argThat(options -> {
          return (options.getWaitForSync() == true)
            && (options.getSilent() == true);
        }));

    verify(collectionMock, never()).insertDocuments(any());
  }

  @Test
  public void writeMultipleCollectionDeleteWritesRecords() {
    // Set up input stubs
    final List<ArangoRecord> arangoRecordStubs = Arrays.asList(
        new ArangoRecord("somecollection", "aKey", null),
        new ArangoRecord("somecollection", "bKey", null),
        new ArangoRecord("othercollection", "cKey", null),
        new ArangoRecord("othercollection", "dKey", null),
        new ArangoRecord("elsecollection", "eKey", null));

    // Set up Writer dependencies
    final ArangoCollection collectionMock = mock(ArangoCollection.class);

    final ArangoDatabase databaseMock = mock(ArangoDatabase.class);
    when(databaseMock.collection(any())).thenReturn(collectionMock);

    // Test system under test
    final Writer writer = new Writer(databaseMock);
    writer.write(arangoRecordStubs);

    InOrder inOrder = Mockito.inOrder(collectionMock);
    inOrder.verify(collectionMock).deleteDocuments(
        eq(Arrays.asList("aKey", "bKey")),
        eq(null),
        argThat(options -> {
          return (options.getWaitForSync() == true)
            && (options.getSilent() == true);
        }));
    inOrder.verify(collectionMock).deleteDocuments(
        eq(Arrays.asList("cKey", "dKey")),
        eq(null),
        argThat(options -> {
          return (options.getWaitForSync() == true)
            && (options.getSilent() == true);
        }));
    inOrder.verify(collectionMock).deleteDocuments(
        eq(Arrays.asList("eKey")),
        eq(null),
        argThat(options -> {
          return (options.getWaitForSync() == true)
            && (options.getSilent() == true);
        }));

    verify(collectionMock, never()).insertDocuments(any());
  }

  @Test
  public void writeMixedWritesRecords() {
    // Set up input stubs
    final List<ArangoRecord> arangoRecordStubs = Arrays.asList(
        new ArangoRecord("somecollection", "aKey", "ajson"),
        new ArangoRecord("somecollection", "bKey", "bjson"),
        new ArangoRecord("othercollection", "cKey", "cjson"),
        new ArangoRecord("othercollection", "dKey", null),
        new ArangoRecord("somecollection", "eKey", null),
        new ArangoRecord("somecollection", "fKey", null),
        new ArangoRecord("somecollection", "gKey", "gjson"));

    // Set up Writer dependencies
    final ArangoCollection collectionMock = mock(ArangoCollection.class);

    final ArangoDatabase databaseMock = mock(ArangoDatabase.class);
    when(databaseMock.collection(any())).thenReturn(collectionMock);

    // Test system under test
    final Writer writer = new Writer(databaseMock);
    writer.write(arangoRecordStubs);

    InOrder inOrder = Mockito.inOrder(collectionMock);
    inOrder.verify(collectionMock).insertDocuments(
        eq(Arrays.asList("ajson", "bjson")),
        argThat(options -> {
          return (options.getOverwrite() == true)
            && (options.getWaitForSync() == true)
            && (options.getSilent() == true);
        }));
    inOrder.verify(collectionMock).insertDocuments(
        eq(Arrays.asList("cjson")),
        argThat(options -> {
          return (options.getOverwrite() == true)
            && (options.getWaitForSync() == true)
            && (options.getSilent() == true);
        }));
    inOrder.verify(collectionMock).deleteDocuments(
        eq(Arrays.asList("dKey")),
        eq(null),
        argThat(options -> {
          return (options.getWaitForSync() == true)
            && (options.getSilent() == true);
        }));
    inOrder.verify(collectionMock).deleteDocuments(
        eq(Arrays.asList("eKey", "fKey")),
        eq(null),
        argThat(options -> {
          return (options.getWaitForSync() == true)
            && (options.getSilent() == true);
        }));
    inOrder.verify(collectionMock).insertDocuments(
        eq(Arrays.asList("gjson")),
        argThat(options -> {
          return (options.getOverwrite() == true)
            && (options.getWaitForSync() == true)
            && (options.getSilent() == true);
        }));
  }
}
