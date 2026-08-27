package io.github.kpsantiago.caca_oinbu.controller.contract;

import io.github.kpsantiago.caca_oinbu.dto.request.UserRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;

public interface IUserController {
    @Operation(summary = "Cria um novo usuárip")
    ResponseEntity<UserResponseDto> create(UserRequestDto request);
}
