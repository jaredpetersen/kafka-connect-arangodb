package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.Type;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import java.util.Objects;

@JsonDeserialize(as = TruncateCollection.class, builder = TruncateCollection.Builder.class)
public class TruncateCollection implements WalEntry {
  private final String tick;
  private final int type = Type.TRUNCATE_COLLECTION.toValue();
  private final String db;
  private final String cuid;

  private TruncateCollection(TruncateCollection.Builder builder) {
    this.tick = builder.tick;
    this.db = builder.db;
    this.cuid = builder.cuid;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TruncateCollection that = (TruncateCollection) o;
    return getType() == that.getType() &&
        Objects.equals(getTick(), that.getTick()) &&
        Objects.equals(getDb(), that.getDb()) &&
        Objects.equals(getCuid(), that.getCuid());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTick(), getType(), getDb(), getCuid());
  }

  @JsonPOJOBuilder(withPrefix = "")
  @JsonIgnoreProperties(value = { "type" })
  public static class Builder {
    private String tick;
    private String db;
    private String cuid;

    public TruncateCollection.Builder tick(String tick) {
      this.tick = tick;
      return this;
    }

    public TruncateCollection.Builder db(String db) {
      this.db = db;
      return this;
    }

    public TruncateCollection.Builder cuid(String cuid) {
      this.cuid = cuid;
      return this;
    }

    public TruncateCollection build() {
      return new TruncateCollection(this);
    }
  }
}
