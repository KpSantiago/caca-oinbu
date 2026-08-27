package io.github.kpsantiago.caca_oinbu.service.contract;

import io.github.kpsantiago.caca_oinbu.dto.response.TokenResponse;
import org.springframework.security.core.Authentication;

public interface ITokenService {
    TokenResponse generateToken(Authentication authentication);
    String getUserFromToken(String token);
    boolean isTokenValid(String token);
}
