package io.github.kpsantiago.caca_oinbu.config;

import io.github.kpsantiago.caca_oinbu.exception.UnauthorizedException;
import io.github.kpsantiago.caca_oinbu.service.contract.IAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.Optional;

@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION = "Authorization";
    private static final String PREFIX = "Bearer";

    private IAuthService authService;
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> token = getTokenFromAuthentication(request);

        if (token.isEmpty() || !authService.isTokenValid(token.get()))
            throw new UnauthorizedException("Token inválido");

        String username = authService.getUserFromToken(token.get());
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    private Optional<String> getTokenFromAuthentication(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);

        if (header.isBlank() || !header.startsWith(PREFIX)) {
            return Optional.empty();
        }

        var token = header.substring(PREFIX.length() + 1);
        return Optional.of(token);
    }

}
