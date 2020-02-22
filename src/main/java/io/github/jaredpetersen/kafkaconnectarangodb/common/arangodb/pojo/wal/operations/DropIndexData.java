package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;

@JsonDeserialize(builder = DropIndexData.Builder.class)
public class DropIndexData {
  private final String id;

  private DropIndexData(Builder builder) {
    this.id = builder.id;
  }

  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DropIndexData that = (DropIndexData) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private String id;

    public DropIndexData.Builder id(String id) {
      this.id = id;
      return this;
    }

    public DropIndexData build() {
      return new DropIndexData(this);
    }
  }
}
