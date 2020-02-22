package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.Type;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import java.util.Objects;

@JsonDeserialize(as = DropView.class, builder = DropView.Builder.class)
public class DropView implements WalEntry {
  private final String tick;
  private final int type = Type.DROP_VIEW.toValue();
  private final String db;
  private final String cuid;

  private DropView(DropView.Builder builder) {
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

  public final String getCuid() {
    return this.cuid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DropView dropView = (DropView) o;
    return getType() == dropView.getType() &&
        Objects.equals(getTick(), dropView.getTick()) &&
        Objects.equals(getDb(), dropView.getDb()) &&
        Objects.equals(getCuid(), dropView.getCuid());
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

    public DropView.Builder tick(String tick) {
      this.tick = tick;
      return this;
    }

    public DropView.Builder db(String db) {
      this.db = db;
      return this;
    }

    public DropView.Builder cuid(String cuid) {
      this.cuid = cuid;
      return this;
    }

    public DropView build() {
      return new DropView(this);
    }
  }
}
