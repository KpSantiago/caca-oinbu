package io.github.kpsantiago.caca_oinbu.service;

import io.github.kpsantiago.caca_oinbu.dto.response.TokenResponse;
import io.github.kpsantiago.caca_oinbu.enums.Role;
import io.github.kpsantiago.caca_oinbu.exception.BadJwtException;
import io.github.kpsantiago.caca_oinbu.model.User;
import io.github.kpsantiago.caca_oinbu.service.contract.ITokenService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class TokenService implements ITokenService {

    @Value("${application.security.jwt.expiration}")
    private Integer expiration;

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    public TokenService(JwtEncoder encoder, JwtDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    @Override
    public TokenResponse generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .filter(a -> a.getAuthority() != null && !a.getAuthority().startsWith("FACTOR_"))
                .map(GrantedAuthority::getAuthority)
                   .collect(Collectors.joining(""));

        User user = (User) authentication.getPrincipal();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("http://localhost:8080")
                .subject(user.getEmail())
                .issuedAt(now)
                .claim("scope", scope)
                .expiresAt(now.plus(expiration, ChronoUnit.MINUTES))
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);

        String token = encoder.encode(parameters).getTokenValue();
        var response = new TokenResponse(
                token,
                Role.valueOf(scope.substring("ROLE_".length())),
                expiration
        );

        return response;
    }

    @Override
    public String getUserFromToken(String token) {
        Jwt jwtToken = decoder.decode(token);

        return jwtToken.getSubject();
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            decoder.decode(token);
            return true;
        } catch(Exception e) {
            throw new BadJwtException("Token inválido");
        }
    }
}
