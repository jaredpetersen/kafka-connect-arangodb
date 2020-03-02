package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import java.util.Objects;

/**
 * Generic WAL entry operation used in place of more specific operations that we don't need to analyze.
 * Allows us to be compatible with future WAL operation types in newer ArangoDB versions.
 */
@JsonDeserialize(as = Operation.class, builder = Operation.Builder.class)
public class Operation implements WalEntry {
  private final String tick;
  private final int type;

  private Operation(Operation.Builder builder) {
    this.tick = builder.tick;
    this.type = builder.type;
  }

  public final String getTick() {
    return this.tick;
  }

  public final int getType() {
    return this.type;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private String tick;
    private int type;

    public Operation.Builder tick(String tick) {
      this.tick = tick;
      return this;
    }

    public Operation.Builder type(int type) {
      this.type = type;
      return this;
    }

    public Operation build() {
      return new Operation(this);
    }
  }
}
