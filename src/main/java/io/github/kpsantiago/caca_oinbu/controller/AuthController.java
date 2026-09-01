package io.github.kpsantiago.caca_oinbu.controller;

import io.github.kpsantiago.caca_oinbu.config.ApiConfig;
import io.github.kpsantiago.caca_oinbu.controller.contract.IAuthController;
import io.github.kpsantiago.caca_oinbu.dto.request.LoginRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.TokenResponse;
import io.github.kpsantiago.caca_oinbu.dto.response.UserResponseDto;
import io.github.kpsantiago.caca_oinbu.model.User;
import io.github.kpsantiago.caca_oinbu.service.contract.IAuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConfig.AUTH_BASE_PATH)
@AllArgsConstructor
public class AuthController implements IAuthController {
    private final IAuthService service;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(service.login(request));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('DRIVER', 'ADMIN', 'USER')")
    public ResponseEntity<UserResponseDto> profile() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(service.profile(user.getEmail()));
    }

}
