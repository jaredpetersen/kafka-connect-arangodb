package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.Type;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import java.util.Objects;

@JsonDeserialize(as = StartTransaction.class, builder = StartTransaction.Builder.class)
public class StartTransaction implements WalEntry {
  private final String tick;
  private final int type = Type.START_TRANSACTION.toValue();
  private final String db;
  private final String tid;

  private StartTransaction(StartTransaction.Builder builder) {
    this.tick = builder.tick;
    this.db = builder.db;
    this.tid = builder.tid;
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

  public final String getTid() {
    return this.tid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StartTransaction that = (StartTransaction) o;
    return getType() == that.getType() &&
        Objects.equals(getTick(), that.getTick()) &&
        Objects.equals(getDb(), that.getDb()) &&
        Objects.equals(getTid(), that.getTid());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTick(), getType(), getDb(), getTid());
  }

  @JsonPOJOBuilder(withPrefix = "")
  @JsonIgnoreProperties(value = { "type" })
  public static class Builder {
    private String tick;
    private String db;
    private String tid;

    public StartTransaction.Builder tick(String tick) {
      this.tick = tick;
      return this;
    }

    public StartTransaction.Builder db(String db) {
      this.db = db;
      return this;
    }

    public StartTransaction.Builder tid(String tid) {
      this.tid = tid;
      return this;
    }

    public StartTransaction build() {
      return new StartTransaction(this);
    }
  }
}
