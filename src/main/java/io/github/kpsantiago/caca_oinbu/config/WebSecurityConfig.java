package io.github.kpsantiago.caca_oinbu.config;

import io.github.kpsantiago.caca_oinbu.enums.Role;
import io.github.kpsantiago.caca_oinbu.service.contract.IAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class WebSecurityConfig {
    private final IAuthService authService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    private final static String BASE_URL_MATCHER = ApiConfig.API_BASE_PATH + "/**";
    private final static String[] whiteList = ApiConfig.WHITE_LIST;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .formLogin(FormLoginConfigurer::disable)
            .authorizeHttpRequests(requests -> requests
                    .requestMatchers(whiteList).permitAll()
                    .requestMatchers(BASE_URL_MATCHER).authenticated()
                    .anyRequest().denyAll()
            )
            .addFilterBefore(getJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .csrf(csrf ->
                    csrf.disable()
                            .sessionManagement(sm ->
                                    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)))
            .authenticationManager(authenticationManager())
            .exceptionHandling(handler ->
                        handler.authenticationEntryPoint((request, response, authException) ->
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN)
                        ));


        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(true);

        return providerManager;
    }

    @Bean
    static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role(Role.ADMIN.name())
                .implies(Role.DRIVER.name())
                .role(Role.DRIVER.name())
                .implies(Role.USER.name())
                .build();
    }

    private JwtAuthenticationFilter getJwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(authService, userDetailsService);
    }
}