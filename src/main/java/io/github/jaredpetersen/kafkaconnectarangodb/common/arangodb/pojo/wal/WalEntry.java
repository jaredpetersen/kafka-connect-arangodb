package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = WalDeserializer.class)
public interface WalEntry {
  String getTick();

  int getType();
}
