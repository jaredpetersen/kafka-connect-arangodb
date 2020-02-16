package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;

@JsonDeserialize(as = CreateDatabase.class, builder = CreateDatabase.Builder.class)
public class CreateDatabase implements WalEntry {
  private final String tick;
  private final Integer type;
  private final String db;
  private final CreateDatabaseData data;

  private CreateDatabase(CreateDatabase.Builder builder) {
    this.tick = builder.tick;
    this.type = builder.type;
    this.db = builder.db;
    this.data = builder.data;
  }

  public final String getTick() {
    return this.tick;
  }

  public final Integer getType() {
    return this.type;
  }

  public final String getDb() {
    return this.db;
  }

  public final CreateDatabaseData getData() {
    return this.data;
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private String tick;
    private Integer type = 0;
    private String db;
    private CreateDatabaseData data;

    public CreateDatabase.Builder tick(String tick) {
      this.tick = tick;
      return this;
    }

    public CreateDatabase.Builder type(Integer type) {
      this.type = type;
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
