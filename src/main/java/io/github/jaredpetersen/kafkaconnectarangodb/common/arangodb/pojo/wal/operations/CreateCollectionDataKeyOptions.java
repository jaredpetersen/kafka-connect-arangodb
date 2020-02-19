package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;

@JsonDeserialize(builder = CreateCollectionDataKeyOptions.Builder.class)
public class CreateCollectionDataKeyOptions {
  private final boolean allowUserKeys;
  private final int lastValue;
  private final String type;

  private CreateCollectionDataKeyOptions(Builder builder) {
    this.allowUserKeys = builder.allowUserKeys;
    this.lastValue = builder.lastValue;
    this.type = builder.type;
  }

  public boolean getAllowUserKeys() {
    return this.allowUserKeys;
  }

  public int getLastValue() {
    return this.lastValue;
  }

  public String getType() {
    return this.type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CreateCollectionDataKeyOptions that = (CreateCollectionDataKeyOptions) o;
    return getAllowUserKeys() == that.getAllowUserKeys() &&
        getLastValue() == that.getLastValue() &&
        Objects.equals(getType(), that.getType());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getAllowUserKeys(), getLastValue(), getType());
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private boolean allowUserKeys;
    private int lastValue = 0;
    private String type;

    public CreateCollectionDataKeyOptions.Builder allowUserKeys(boolean allowUserKeys) {
      this.allowUserKeys = allowUserKeys;
      return this;
    }

    public CreateCollectionDataKeyOptions.Builder lastValue(int lastValue) {
      this.lastValue = lastValue;
      return this;
    }

    public CreateCollectionDataKeyOptions.Builder type(String type) {
      this.type = type;
      return this;
    }

    public CreateCollectionDataKeyOptions build() {
      return new CreateCollectionDataKeyOptions(this);
    }
  }
}
