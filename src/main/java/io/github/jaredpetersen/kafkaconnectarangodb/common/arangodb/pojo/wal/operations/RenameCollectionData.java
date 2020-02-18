package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;

@JsonDeserialize(builder = RenameCollectionData.Builder.class)
public class RenameCollectionData {
  private final String name;

  private RenameCollectionData(Builder builder) {
    this.name = builder.name;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RenameCollectionData that = (RenameCollectionData) o;
    return Objects.equals(getName(), that.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName());
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private String name;

    public RenameCollectionData.Builder name(String name) {
      this.name = name;
      return this;
    }

    public RenameCollectionData build() {
      return new RenameCollectionData(this);
    }
  }
}
