package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = CreateDatabaseData.Builder.class)
public class CreateDatabaseData {
  private final Long database;
  private final String id;
  private final String name;

  private CreateDatabaseData(Builder builder) {
    this.database = builder.database;
    this.id = builder.id;
    this.name = builder.name;
  }

  public Long getDatabase() {
    return database;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private Long database;
    private String id;
    private String name;

    public CreateDatabaseData.Builder database(Long database) {
      this.database = database;
      return this;
    }

    public CreateDatabaseData.Builder id(String id) {
      this.id = id;
      return this;
    }

    public CreateDatabaseData.Builder name(String name) {
      this.name = name;
      return this;
    }

    public CreateDatabaseData build() {
      return new CreateDatabaseData(this);
    }
  }
}
