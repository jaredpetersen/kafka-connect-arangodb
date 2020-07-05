package io.github.jaredpetersen.kafkaconnectarangodb.common.arangodb.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class JwtAuthInterceptor implements RequestInterceptor {
    private final String jwt;

    public JwtAuthInterceptor(String jwt) {
        this.jwt = jwt;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Authorization", "bearer " + this.jwt);
    }
}
