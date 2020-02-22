package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.Type;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import java.util.Objects;

@JsonDeserialize(as = DropIndex.class, builder = DropIndex.Builder.class)
public class DropIndex implements WalEntry {
  private final String tick;
  private final int type = Type.DROP_INDEX.toValue();
  private final String db;
  private final String cuid;
  private final DropIndexData data;

  private DropIndex(DropIndex.Builder builder) {
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

  public final String getCuid() {
    return this.cuid;
  }

  public final DropIndexData getData() {
    return this.data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DropIndex dropIndex = (DropIndex) o;
    return getType() == dropIndex.getType() &&
        Objects.equals(getTick(), dropIndex.getTick()) &&
        Objects.equals(getDb(), dropIndex.getDb()) &&
        Objects.equals(getCuid(), dropIndex.getCuid()) &&
        Objects.equals(getData(), dropIndex.getData());
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
    private DropIndexData data;

    public DropIndex.Builder tick(String tick) {
      this.tick = tick;
      return this;
    }

    public DropIndex.Builder db(String db) {
      this.db = db;
      return this;
    }

    public DropIndex.Builder cuid(String cuid) {
      this.cuid = cuid;
      return this;
    }

    public DropIndex.Builder data(DropIndexData data) {
      this.data = data;
      return this;
    }

    public DropIndex build() {
      return new DropIndex(this);
    }
  }
}
