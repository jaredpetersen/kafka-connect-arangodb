package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.Type;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import java.util.Objects;

@JsonDeserialize(as = RenameCollection.class, builder = RenameCollection.Builder.class)
public class RenameCollection implements WalEntry {
  private final String tick;
  private final int type = Type.RENAME_COLLECTION.toValue();
  private final String db;
  private final String cuid;
  private final RenameCollectionData data;

  private RenameCollection(RenameCollection.Builder builder) {
    this.tick = builder.tick;
    this.db = builder.db;
    this.cuid = builder.cuid;
    this.data = builder.data;
  }

  public final String getTick() {
    return this.tick;
  }

  public final int getType() {
    return this.type;
  }

  public final String getDb() {
    return this.db;
  }

  public String getCuid() {
    return this.cuid;
  }

  public RenameCollectionData getData() {
    return this.data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RenameCollection that = (RenameCollection) o;
    return getType() == that.getType() &&
        Objects.equals(getTick(), that.getTick()) &&
        Objects.equals(getDb(), that.getDb()) &&
        Objects.equals(getCuid(), that.getCuid()) &&
        Objects.equals(getData(), that.getData());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTick(), getType(), getDb(), getCuid(), getData());
  }

  @JsonPOJOBuilder(withPrefix = "")
  @JsonIgnoreProperties(value = { "type" })
  public static class Builder {
    private String tick;
    private String db;
    private String cuid;
    private RenameCollectionData data;

    public RenameCollection.Builder tick(String tick) {
      this.tick = tick;
      return this;
    }

    public RenameCollection.Builder db(String db) {
      this.db = db;
      return this;
    }

    public RenameCollection.Builder cuid(String cuid) {
      this.cuid = cuid;
      return this;
    }

    public RenameCollection.Builder data(RenameCollectionData data) {
      this.data = data;
      return this;
    }

    public RenameCollection build() {
      return new RenameCollection(this);
    }
  }
}
