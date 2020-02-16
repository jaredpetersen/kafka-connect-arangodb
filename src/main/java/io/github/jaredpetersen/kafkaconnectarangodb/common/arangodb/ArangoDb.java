package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb;

public class ArangoDb {
  private final String host;
  private final String jwt;

  private ArangoDb(Builder builder) {
    this.host = builder.host;
    this.jwt = builder.jwt;
  }

  public static class Builder {
    private String host;
    private String jwt;

    public Builder host(String host) {
      this.host = host;

      return this;
    }

    public Builder jwt(String jwt) {
      this.jwt = jwt;
      return this;
    }
  }

//  public List<>
}
