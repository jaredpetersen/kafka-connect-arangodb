package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;

@JsonDeserialize(builder = CreateCollectionData.Builder.class)
public class ChangeCollectionData {
  private final boolean waitForSync;

  private ChangeCollectionData(Builder builder) {
    this.waitForSync = builder.waitForSync;
  }

  public boolean isWaitForSync() {
    return waitForSync;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangeCollectionData that = (ChangeCollectionData) o;
    return isWaitForSync() == that.isWaitForSync();
  }

  @Override
  public int hashCode() {
    return Objects.hash(isWaitForSync());
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private boolean waitForSync = false;

    public ChangeCollectionData.Builder waitForSync(boolean waitForSync) {
      this.waitForSync = waitForSync;
      return this;
    }

    public ChangeCollectionData build() {
      return new ChangeCollectionData(this);
    }
  }
}
