package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.Type;

import java.util.Objects;

public class ChangeCollection {
  private final String tick;
  private final int type = Type.CHANGE_COLLECTION.toValue();
  private final String db;
  private final String cuid;
  private final ChangeCollectionData data;

  private ChangeCollection(ChangeCollection.Builder builder) {
    this.tick = builder.tick;
    this.db = builder.db;
    this.cuid = builder.cuid;
    this.data = builder.data;
  }

  public String getTick() {
    return tick;
  }

  public int getType() {
    return type;
  }

  public String getDb() {
    return db;
  }

  public String getCuid() {
    return cuid;
  }

  public ChangeCollectionData getData() {
    return data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangeCollection that = (ChangeCollection) o;
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
    private int type = Type.CHANGE_COLLECTION.toValue();
    private String db;
    private String cuid;
    private ChangeCollectionData data;

    public Builder tick(String tick) {
      this.tick = tick;
      return this;
    }

    public Builder type(int type) {
      this.type = type;
      return this;
    }

    public Builder db(String db) {
      this.db = db;
      return this;
    }

    public Builder cuid(String cuid) {
      this.cuid = cuid;
      return this;
    }

    public Builder data(ChangeCollectionData data) {
      this.data = data;
      return this;
    }

    public ChangeCollection build() {
      return new ChangeCollection(this);
    }
  }
}
