package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.Type;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import java.util.Objects;

@JsonDeserialize(as = CreateCollection.class, builder = CreateCollection.Builder.class)
public class CreateCollection implements WalEntry {
  private final String tick;
  private final int type = Type.CREATE_COLLECTION.toValue();
  private final String db;
  private final String cuid;
  private final String name;

  private CreateCollection(CreateCollection.Builder builder) {
    this.tick = builder.tick;
    this.db = builder.db;
    this.cuid = builder.cuid;
    this.name = builder.name;
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

  public final String getName() {
    return this.name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CreateCollection that = (CreateCollection) o;
    return getType() == that.getType() &&
        Objects.equals(getTick(), that.getTick()) &&
        Objects.equals(getDb(), that.getDb()) &&
        Objects.equals(getCuid(), that.getCuid()) &&
        Objects.equals(getName(), that.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTick(), getType(), getDb(), getCuid(), getName());
  }

  @JsonPOJOBuilder(withPrefix = "")
  @JsonIgnoreProperties(value = { "type" })
  public static class Builder {
    private String tick;
    private String db;
    private String cuid;
    private String name;

    public CreateCollection.Builder tick(String tick) {
      this.tick = tick;
      return this;
    }

    public CreateCollection.Builder db(String db) {
      this.db = db;
      return this;
    }

    public CreateCollection.Builder cuid(String cuid) {
      this.cuid = cuid;
      return this;
    }

    public CreateCollection.Builder name(String name) {
      this.name = name;
      return this;
    }

    public CreateCollection build() {
      return new CreateCollection(this);
    }
  }
}
