package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = Deserializer.class)
public interface WalEntry {
  String getTick();

  Integer getType();

  String getDb();
}
