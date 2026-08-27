package io.github.kpsantiago.caca_oinbu.service.contract;

import io.github.kpsantiago.caca_oinbu.dto.request.LoginRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.TokenResponse;
import io.github.kpsantiago.caca_oinbu.dto.response.UserResponseDto;

public interface IAuthService {
    TokenResponse login(LoginRequestDto request);
    String getUserFromToken(String token);
    boolean isTokenValid(String token);
    UserResponseDto profile(String userEmail);
}
