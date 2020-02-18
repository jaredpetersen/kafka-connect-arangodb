package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.operations;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import java.util.Objects;

@JsonDeserialize(builder = CreateCollectionData.Builder.class)
public class CreateCollectionData {
  private final boolean allowUserKeys;
  private final boolean cacheEnabled;
  private final String cid;
  private final boolean deleted;
  private final String globallyUniqueId;
  private final String id;
  private final List<String> indexes;
  private final boolean isSystem;
  private final CreateCollectionDataKeyOptions keyOptions;

  private CreateCollectionData(Builder builder) {
    this.allowUserKeys = builder.allowUserKeys;
    this.cacheEnabled = builder.cacheEnabled;
    this.cid = builder.cid;
    this.deleted = builder.deleted;
    this.globallyUniqueId = builder.globallyUniqueId;
    this.id = builder.id;
    this.indexes = builder.indexes;
    this.isSystem = builder.isSystem;
    this.keyOptions = builder.keyOptions;
  }

  public boolean isAllowUserKeys() {
    return allowUserKeys;
  }

  public boolean isCacheEnabled() {
    return cacheEnabled;
  }

  public String getCid() {
    return cid;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public String getGloballyUniqueId() {
    return globallyUniqueId;
  }

  public String getId() {
    return id;
  }

  public List<String> getIndexes() {
    return indexes;
  }

  public boolean isSystem() {
    return isSystem;
  }

  public CreateCollectionDataKeyOptions getKeyOptions() {
    return keyOptions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CreateCollectionData that = (CreateCollectionData) o;
    return isAllowUserKeys() == that.isAllowUserKeys() &&
        isCacheEnabled() == that.isCacheEnabled() &&
        isDeleted() == that.isDeleted() &&
        isSystem() == that.isSystem() &&
        Objects.equals(getCid(), that.getCid()) &&
        Objects.equals(getGloballyUniqueId(), that.getGloballyUniqueId()) &&
        Objects.equals(getId(), that.getId()) &&
        Objects.equals(getIndexes(), that.getIndexes()) &&
        Objects.equals(getKeyOptions(), that.getKeyOptions());
  }

  @Override
  public int hashCode() {
    return Objects.hash(isAllowUserKeys(), isCacheEnabled(), getCid(), isDeleted(), getGloballyUniqueId(), getId(), getIndexes(), isSystem(), getKeyOptions());
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static class Builder {
    private boolean allowUserKeys = false;
    private boolean cacheEnabled = false;
    private String cid;
    private boolean deleted = false;
    private String globallyUniqueId;
    private String id;
    private List<String> indexes;
    private boolean isSystem = false;
    private CreateCollectionDataKeyOptions keyOptions;

    public CreateCollectionData.Builder allowUserKeys(boolean allowUserKeys) {
      this.allowUserKeys = allowUserKeys;
      return this;
    }

    public CreateCollectionData.Builder cacheEnabled(boolean cacheEnabled) {
      this.cacheEnabled = cacheEnabled;
      return this;
    }

    public CreateCollectionData.Builder cid(String cid) {
      this.cid = cid;
      return this;
    }

    public CreateCollectionData.Builder deleted(boolean deleted) {
      this.deleted = deleted;
      return this;
    }

    public CreateCollectionData.Builder globallyUniqueId(String globallyUniqueId) {
      this.globallyUniqueId = globallyUniqueId;
      return this;
    }

    public CreateCollectionData.Builder id(String id) {
      this.id = id;
      return this;
    }

    public CreateCollectionData.Builder indexes(List<String> indexes) {
      this.indexes = indexes;
      return this;
    }

    public CreateCollectionData.Builder isSystem(boolean isSystem) {
      this.isSystem = isSystem;
      return this;
    }

    public CreateCollectionData.Builder keyOptions(CreateCollectionDataKeyOptions keyOptions) {
      this.keyOptions = keyOptions;
      return this;
    }

    public CreateCollectionData build() {
      return new CreateCollectionData(this);
    }
  }
}
