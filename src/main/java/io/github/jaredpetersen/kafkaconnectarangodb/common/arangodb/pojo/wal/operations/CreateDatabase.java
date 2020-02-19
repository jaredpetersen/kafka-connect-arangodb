package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.Type;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import java.util.Objects;

@JsonDeserialize(as = CreateDatabase.class, builder = CreateDatabase.Builder.class)
public class CreateDatabase implements WalEntry {
  private final String tick;
  private final int type = Type.CREATE_DATABASE.toValue();
  private final String db;
  private final CreateDatabaseData data;

  private CreateDatabase(CreateDatabase.Builder builder) {
    this.tick = builder.tick;
    this.db = builder.db;
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

  public final CreateDatabaseData getData() {
    return this.data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CreateDatabase that = (CreateDatabase) o;
    return getType() == that.getType() &&
        Objects.equals(getTick(), that.getTick()) &&
        Objects.equals(getDb(), that.getDb()) &&
        Objects.equals(getData(), that.getData());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTick(), getType(), getDb(), getData());
  }

  @JsonPOJOBuilder(withPrefix = "")
  @JsonIgnoreProperties(value = { "type" })
  public static class Builder {
    private String tick;
    private String db;
    private CreateDatabaseData data;

    public CreateDatabase.Builder tick(String tick) {
      this.tick = tick;
      return this;
    }

    public CreateDatabase.Builder db(String db) {
      this.db = db;
      return this;
    }

    public CreateDatabase.Builder data(CreateDatabaseData data) {
      this.data = data;
      return this;
    }

    public CreateDatabase build() {
      return new CreateDatabase(this);
    }
  }
}
