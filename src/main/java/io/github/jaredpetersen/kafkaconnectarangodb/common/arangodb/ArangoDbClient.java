package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb;

import feign.Param;
import feign.RequestLine;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;

import java.util.List;

interface ArangoDbClient {
    @RequestLine("GET /_wal/tail?from={from}")
    List<WalEntry> tailWal(@Param("from") long from);
}
