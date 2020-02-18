package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;

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

  public boolean isAllowUserKeys() {
    return this.allowUserKeys;
  }

  public int getLastValue() {
    return this.lastValue;
  }

  public String getType() {
    return this.type;
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
