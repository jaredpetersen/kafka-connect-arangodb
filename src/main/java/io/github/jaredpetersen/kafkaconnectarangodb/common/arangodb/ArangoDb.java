package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.pojo.wal.WalEntry;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.List;

public class ArangoDb {
  private final String scheme;
  private final String host;
  private final int port;
  private final String jwt;
  private final OkHttpClient client;
  private final ObjectMapper mapper;

  private ArangoDb(Builder builder) {
    this.scheme = builder.scheme;
    this.host = builder.host;
    this.port = builder.port;
    this.jwt = builder.jwt;

    this.client = new OkHttpClient();
    this.mapper = new ObjectMapper();
  }

  public static class Builder {
    private String scheme = "http";
    private String host;
    private int port = 8529;
    private String jwt;

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

    public ArangoDb build() {
      return new ArangoDb(this);
    }
  }

  /**
   * Tail the write-ahead log and return all operations.
   * @param from Lower bound tick value for results.
   * @return All write-ahead log operations.
   * @throws IOException
   */
  public List<WalEntry> tailWal(Long from) throws IOException {
    HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
        .scheme("http")
        .host(this.host)
        .port(this.port)
        .addPathSegments("_api/_wal/tail");

    if (from != null) {
      urlBuilder.addQueryParameter("from", String.valueOf(from));
    }

    HttpUrl url = urlBuilder.build();

    Request request = new Request.Builder()
        .url(url)
        .addHeader("authorization", "bearer " + this.jwt)
        .addHeader("accept", "application/json")
        .build();

    try (Response response = client.newCall(request).execute()) {
      String responseBody = response.body().string();
      MappingIterator<WalEntry> iterator = mapper.readerFor(WalEntry.class).readValues(responseBody);
      List<WalEntry> walEntries = iterator.readAll();

      return walEntries;
    }
  }
}
