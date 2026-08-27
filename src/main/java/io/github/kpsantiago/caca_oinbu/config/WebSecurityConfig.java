package io.github.kpsantiago.caca_oinbu.config;

import io.github.kpsantiago.caca_oinbu.service.contract.IAuthService;
import jakarta.servlet.Filter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class WebSecurityConfig {
    private final IAuthService authService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    private final static String LOGIN_URL_MATCHER = ApiConfig.AUTH_BASE_PATH + "/login";
    private final static String BASE_URL_MATCHER = ApiConfig.API_BASE_PATH + "/**";
    private final static String[] swaggerPaths = {
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui.html/**",
            "/swagger-ui/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        Filter filter = getJwtAuthenticationFilter();

        http
            .formLogin(FormLoginConfigurer::disable)
            .csrf(CsrfConfigurer::disable)
            .authorizeHttpRequests(requests ->
                    requests
                            .requestMatchers(HttpMethod.POST, LOGIN_URL_MATCHER, ApiConfig.USER_BASE_PATH).permitAll()
                            .requestMatchers(swaggerPaths).permitAll()
                            .requestMatchers(BASE_URL_MATCHER).authenticated()
                            .anyRequest().denyAll()
            ).addFilterAfter(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    private JwtAuthenticationFilter getJwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(authService, userDetailsService);
    }
}
