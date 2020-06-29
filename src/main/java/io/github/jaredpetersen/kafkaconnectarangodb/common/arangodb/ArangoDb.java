package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.auth.JwtAuthInterceptor;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;

import java.util.List;

/**
 * ArangoDB client library.
 */
public class ArangoDb {
    private final ArangoDbClient client;

    private ArangoDb(Builder builder) {
        final String url = builder.scheme
            + builder.host
            + builder.port
            + "/_db"
            + builder.database
            + "/_api";

        this.client = Feign.builder()
            .client(new OkHttpClient())
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .requestInterceptor(new JwtAuthInterceptor(builder.jwt))
            .target(ArangoDbClient.class, url);
    }

    /**
     * Tail the write-ahead log and return all operations.
     * @param from Lower bound tick value for results.
     * @return All write-ahead log operations.
     */
    public List<WalEntry> tailWal(long from) {
        return this.client.tailWal(from);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String scheme = "http";
        private String host;
        private int port = 8529;
        private String jwt;
        private String database;

        private Builder() {}

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port (int port) {
            this.port = port;
            return this;
        }

        public Builder ssl(boolean useSsl) {
            this.scheme = (useSsl) ? "https" : "http";
            return this;
        }

        public Builder jwt(String jwt) {
            this.jwt = jwt;
            return this;
        }

        public Builder database(String database) {
            this.database = database;
            return this;
        }

        public ArangoDb build() {
            return new ArangoDb(this);
        }
    }
}
