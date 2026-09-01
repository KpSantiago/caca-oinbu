package io.github.kpsantiago.caca_oinbu.config;

public class ApiConfig {
    public final static String API_PATH = "/api";
    public final static String API_VERSION = "/v1";

    public final static String API_BASE_PATH = API_PATH + API_VERSION;

    public final static String AUTH_BASE_PATH = API_BASE_PATH + "/auth";
    public final static String USER_BASE_PATH = API_BASE_PATH + "/users";
    public final static String BUS_BASE_PATH = API_BASE_PATH + "/bus";

    public final static String[] WHITE_LIST = {
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui.html/**",
            "/swagger-ui/**",
            AUTH_BASE_PATH + "/login",
            USER_BASE_PATH + "/register"
    };

}
