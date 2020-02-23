package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import java.util.Objects;

@JsonDeserialize(as = RemoveDocument.class, builder = RemoveDocument.Builder.class)
public class RemoveDocument implements WalEntry {
  private final String tick;
  private final int type;
  private final String db;
  private final String tid;
  private final String cuid;
  private final ObjectNode data;

  private RemoveDocument(RemoveDocument.Builder builder) {
    this.tick = builder.tick;
    this.type = builder.type;
    this.db = builder.db;
    this.tid = builder.tid;
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

  public final String getTid() {
    return this.tid;
  }

  public String getCuid() {
    return cuid;
  }

  public final ObjectNode getData() {
    return this.data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RemoveDocument that = (RemoveDocument) o;
    return getType() == that.getType() &&
        Objects.equals(getTick(), that.getTick()) &&
        Objects.equals(getDb(), that.getDb()) &&
        Objects.equals(getTid(), that.getTid()) &&
        Objects.equals(getCuid(), that.getCuid()) &&
        Objects.equals(getData(), that.getData());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTick(), getType(), getDb(), getTid(), getCuid(), getData());
  }

  @JsonPOJOBuilder(withPrefix = "")
  @JsonIgnoreProperties(value = { "type" })
  public static class Builder {
    private String tick;
    private int type;
    private String db;
    private String tid;
    private String cuid;
    private ObjectNode data;

    public RemoveDocument.Builder tick(String tick) {
      this.tick = tick;
      return this;
    }

    public RemoveDocument.Builder tick(int type) {
      this.type = type;
      return this;
    }

    public RemoveDocument.Builder db(String db) {
      this.db = db;
      return this;
    }

    public RemoveDocument.Builder tid(String tid) {
      this.tid = tid;
      return this;
    }

    public RemoveDocument.Builder cuid(String cuid) {
      this.cuid = cuid;
      return this;
    }

    public RemoveDocument.Builder data(ObjectNode data) {
      this.data = data;
      return this;
    }

    public RemoveDocument build() {
      return new RemoveDocument(this);
    }
  }
}
