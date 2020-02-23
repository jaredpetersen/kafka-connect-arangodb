package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import java.util.Objects;

@JsonDeserialize(as = AbortTransaction.class, builder = AbortTransaction.Builder.class)
public class AbortTransaction implements WalEntry {
  private final String tick;
  private final int type;
  private final String db;
  private final String tid;

  private AbortTransaction(AbortTransaction.Builder builder) {
    this.tick = builder.tick;
    this.type = builder.type;
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
    AbortTransaction that = (AbortTransaction) o;
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
  public static class Builder {
    private String tick;
    private int type;
    private String db;
    private String tid;

    public AbortTransaction.Builder tick(String tick) {
      this.tick = tick;
      return this;
    }

    public AbortTransaction.Builder type(int type) {
      this.type = type;
      return this;
    }

    public AbortTransaction.Builder db(String db) {
      this.db = db;
      return this;
    }

    public AbortTransaction.Builder tid(String tid) {
      this.tid = tid;
      return this;
    }

    public AbortTransaction build() {
      return new AbortTransaction(this);
    }
  }
}
