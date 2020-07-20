package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Response;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.auth.JwtAuthInterceptor;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ArangoDB client library.
 */
public class ArangoDb {
    private final ArangoDbClient client;

    private ArangoDb(Builder builder) {
        final String baseUrl = (builder.baseUrl.endsWith("/"))
            ? builder.baseUrl
            : builder.baseUrl + "/";
        final String url = baseUrl
            + "_db/"
            + builder.database
            + "/_api";

        final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.client = Feign.builder()
            .client(new OkHttpClient())
            .encoder(new JacksonEncoder(objectMapper))
            .decoder(new JacksonDecoder(objectMapper))
            .mapAndDecode(this::preDecoder, new JacksonDecoder(objectMapper))
            .requestInterceptor(new JwtAuthInterceptor(builder.jwt))
            .target(ArangoDbClient.class, url);
    }

    /**
     * Tail the write-ahead log and return all operations.
     * @param from Lower bound tick value for results.
     * @return All write-ahead log operations.
     */
    public List<WalEntry> tailWal(Long from) {
        return this.client.tailWal(from);
    }

    private Response preDecoder(Response response, Type type) {
        final Response processedResponse;

        if (response.headers().get("Content-Type").contains("application/x-arango-dump; charset=utf-8")) {
            // x-arango-dump uses ndjson/jsonlines format and the feign jackson encoder does not support this
            final String standardJson;
            try (final Reader reader = response.body().asReader(StandardCharsets.UTF_8)) {
                final BufferedReader bufferedReader = new BufferedReader(reader);
                standardJson = "[" + bufferedReader.lines().collect(Collectors.joining(",")) + "]";
            } catch (IOException exception) {
                throw new RuntimeException("failed to decode x-arango-dump response");
            }
            processedResponse = response.toBuilder()
                .body(standardJson, StandardCharsets.UTF_8)
                .build();
        } else {
            processedResponse = response;
        }

        return processedResponse;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String baseUrl;
        private String jwt;
        private String database;

        private Builder() {}

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
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
