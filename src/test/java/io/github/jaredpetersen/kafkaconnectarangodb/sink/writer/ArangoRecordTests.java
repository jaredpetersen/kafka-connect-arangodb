package io.github.jaredpetersen.kafkaconnectarangodb.sink.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ArangoRecordTests {
  @Test
  public void getCollectionReturnsCollection() {
    final ArangoRecord record = new ArangoRecord("somecollection", "somekey", "somevalue");

    assertEquals("somecollection", record.getCollection());
  }

  @Test
  public void getKeyReturnsKey() {
    final ArangoRecord record = new ArangoRecord("somecollection", "somekey", "somevalue");

    assertEquals("somekey", record.getKey());
  }

  @Test
  public void getValueReturnsValue() {
    final ArangoRecord record = new ArangoRecord("somecollection", "somekey", "somevalue");

    assertEquals("somevalue", record.getValue());
  }

  @Test
  public void equalsNullReturnsFalse() {
    final ArangoRecord record = new ArangoRecord("somecollection", "somekey", "somevalue");

    assertFalse(record.equals(null));
  }

  @Test
  public void equalsSameObjectReturnsTrue() {
    final ArangoRecord record = new ArangoRecord("somecollection", "somekey", "somevalue");

    assertTrue(record.equals(record));
  }

  @Test
  public void equalsDifferentObjectTypeReturnsFalse() {
    final ArangoRecord record = new ArangoRecord("somecollection", "somekey", "somevalue");

    assertFalse(record.equals("something else"));
  }

  @Test
  public void equalsEqualObjectReturnsTrue() {
    final ArangoRecord recordA = new ArangoRecord("somecollection", "somekey", "somevalue");
    final ArangoRecord recordB = new ArangoRecord("somecollection", "somekey", "somevalue");

    assertTrue(recordA.equals(recordB));
  }

  @Test
  public void equalsDifferentCollectionReturnsFalse() {
    final ArangoRecord recordA = new ArangoRecord("somecollection", "somekey", "somevalue");
    final ArangoRecord recordB = new ArangoRecord("DIFFERENT", "somekey", "somevalue");

    assertFalse(recordA.equals(recordB));
  }

  @Test
  public void equalsDifferentKeyReturnsFalse() {
    final ArangoRecord recordA = new ArangoRecord("somecollection", "somekey", "somevalue");
    final ArangoRecord recordB = new ArangoRecord("somecollection", "DIFFERENT", "somevalue");

    assertFalse(recordA.equals(recordB));
  }

  @Test
  public void equalsDifferentValueReturnsFalse() {
    final ArangoRecord recordA = new ArangoRecord("somecollection", "somekey", "somevalue");
    final ArangoRecord recordB = new ArangoRecord("somecollection", "somekey", "DIFFERENT");

    assertFalse(recordA.equals(recordB));
  }

  @Test
  public void equalsDifferentValuesReturnsFalse() {
    final ArangoRecord recordA = new ArangoRecord("somecollection", "somekey", "somevalue");
    final ArangoRecord recordB = new ArangoRecord("DIFFERENT", "DIFFERENT", "DIFFERENT");

    assertFalse(recordA.equals(recordB));
  }

  @Test
  public void hashCodeSameValuesReturnsSame() {
    final ArangoRecord recordA = new ArangoRecord("somecollection", "somekey", "somevalue");
    final ArangoRecord recordB = new ArangoRecord("somecollection", "somekey", "somevalue");

    assertEquals(recordA.hashCode(), recordB.hashCode());
  }

  @Test
  public void hashCodeDifferentCollectionReturnsDifferent() {
    final ArangoRecord recordA = new ArangoRecord("somecollection", "somekey", "somevalue");
    final ArangoRecord recordB = new ArangoRecord("DIFFERENT", "somekey", "somevalue");

    assertNotEquals(recordA.hashCode(), recordB.hashCode());
  }

  @Test
  public void hashCodeDifferentKeyReturnsDifferent() {
    final ArangoRecord recordA = new ArangoRecord("somecollection", "somekey", "somevalue");
    final ArangoRecord recordB = new ArangoRecord("somecollection", "DIFFERENT", "somevalue");

    assertNotEquals(recordA.hashCode(), recordB.hashCode());
  }

  @Test
  public void hashCodeDifferentValueReturnsDifferent() {
    final ArangoRecord recordA = new ArangoRecord("somecollection", "somekey", "somevalue");
    final ArangoRecord recordB = new ArangoRecord("somecollection", "somekey", "DIFFERENT");

    assertNotEquals(recordA.hashCode(), recordB.hashCode());
  }

  @Test
  public void hashCodeDifferentValuesReturnsDifferent() {
    final ArangoRecord recordA = new ArangoRecord("somecollection", "somekey", "somevalue");
    final ArangoRecord recordB = new ArangoRecord("DIFFERENT", "DIFFERENT", "DIFFERENT");

    assertNotEquals(recordA.hashCode(), recordB.hashCode());
  }

  @Test
  public void toStringReturnsStringified() {
    final ArangoRecord record = new ArangoRecord("somecollection", "somekey", "somevalue");
    final String expectedStringifiedRecords = "ArangoRecord{collection=somecollection, key=somekey, value=somevalue}";

    assertEquals(expectedStringifiedRecords, record.toString());
  }
}
