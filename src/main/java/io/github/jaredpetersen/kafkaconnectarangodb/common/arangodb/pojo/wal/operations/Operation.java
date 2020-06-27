package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Generic WAL entry operation used in place of more specific operations that we don't need to analyze.
 * Allows us to be compatible with future WAL operation types in newer ArangoDB versions.
 */
@Value
@Builder(builderClassName = "Builder")
@JsonDeserialize(as = Operation.class, builder = Operation.Builder.class)
public class Operation implements WalEntry {
  String tick;
  int type;

  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {}
}
