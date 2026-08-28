package io.github.kpsantiago.caca_oinbu.config;

public class ApiConfig {
    public final static String API_BASE_PATH = "/api";
    public final static String AUTH_BASE_PATH = API_BASE_PATH + "/auth";
    public final static String USER_BASE_PATH = API_BASE_PATH + "/users";
    public final static String[] SWAGGER_PATHS = {
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui.html/**",
            "/swagger-ui/**"
    };
}
