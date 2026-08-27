package io.github.kpsantiago.caca_oinbu.controller.contract;

import io.github.kpsantiago.caca_oinbu.dto.request.LoginRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.TokenResponse;
import io.github.kpsantiago.caca_oinbu.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;

public interface IAuthController {
    @Operation(summary = "Faz o login de um usuário")
    ResponseEntity<TokenResponse> login(LoginRequestDto request);

    @Operation(summary = "Retorna os dados de perfil do usuário")
    ResponseEntity<UserResponseDto> profile();
}
