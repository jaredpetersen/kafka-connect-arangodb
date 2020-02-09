package io.github.jaredpetersen.kafkaconnectarangodb.sink.writer;

import java.util.Objects;

/**
 * Store information needed to write data to an ArangoDB database.
 */
public class ArangoRecord {
  private final String collection;
  private final String key;
  private final String value;

  /**
   * Construct a new ArangoDB Record.
   * @param collection Database collection
   * @param key Document key
   * @param value Document value
   */
  public ArangoRecord(final String collection, final String key, final String value) {
    this.collection = collection;
    this.key = key;
    this.value = value;
  }

  /**
   * Get the record collection.
   * @return Record database collection
   */
  public final String getCollection() {
    return this.collection;
  }

  /**
   * Get the record key.
   * @return Record document key
   */
  public final String getKey() {
    return this.key;
  }

  /**
   * Get the record value.
   * May be null to indicate a deletion.
   * @return Record document value
   */
  public final String getValue() {
    return this.value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArangoRecord that = (ArangoRecord) o;
    return Objects.equals(getCollection(), that.getCollection())
            && Objects.equals(getKey(), that.getKey())
            && Objects.equals(getValue(), that.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getCollection(), getKey(), getValue());
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "{"
      + "collection=" + this.getCollection() + ", "
      + "key=" + this.getKey() + ", "
      + "value=" + this.getValue()
      + "}";
  }
}
