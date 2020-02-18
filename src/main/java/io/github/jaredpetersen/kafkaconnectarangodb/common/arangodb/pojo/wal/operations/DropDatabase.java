package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.Type;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import java.util.Objects;

@JsonDeserialize(as = DropDatabase.class, builder = DropDatabase.Builder.class)
public class DropDatabase implements WalEntry {
  private final String tick;
  private final int type = Type.DROP_DATABASE.toValue();
  private final String db;

  private DropDatabase(DropDatabase.Builder builder) {
    this.tick = builder.tick;
    this.db = builder.db;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DropDatabase that = (DropDatabase) o;
    return getType() == that.getType() &&
        Objects.equals(getTick(), that.getTick()) &&
        Objects.equals(getDb(), that.getDb());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTick(), getType(), getDb());
  }

  @JsonPOJOBuilder(withPrefix = "")
  @JsonIgnoreProperties(value = { "type" })
  public static class Builder {
    private String tick;
    private String db;

    public DropDatabase.Builder tick(String tick) {
      this.tick = tick;
      return this;
    }

    public DropDatabase.Builder db(String db) {
      this.db = db;
      return this;
    }

    public DropDatabase build() {
      return new DropDatabase(this);
    }
  }
}
