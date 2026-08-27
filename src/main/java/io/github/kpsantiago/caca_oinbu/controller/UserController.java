package io.github.kpsantiago.caca_oinbu.controller;

import io.github.kpsantiago.caca_oinbu.config.ApiConfig;
import io.github.kpsantiago.caca_oinbu.controller.contract.IUserController;
import io.github.kpsantiago.caca_oinbu.dto.request.UserRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.UserResponseDto;
import io.github.kpsantiago.caca_oinbu.service.contract.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping(ApiConfig.USER_BASE_PATH)
@AllArgsConstructor
public class UserController implements IUserController {
    private final IUserService service;

    @Override
    @PostMapping
    public ResponseEntity<UserResponseDto> create(UserRequestDto request) {
        return created(URI.create("")).body(service.create(request));
    }
}
