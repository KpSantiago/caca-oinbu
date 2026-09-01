package io.github.kpsantiago.caca_oinbu.service;

import io.github.kpsantiago.caca_oinbu.dto.request.LoginRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.request.UserRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.TokenResponse;
import io.github.kpsantiago.caca_oinbu.dto.response.UserResponseDto;
import io.github.kpsantiago.caca_oinbu.enums.Role;
import io.github.kpsantiago.caca_oinbu.exception.BadRequestException;
import io.github.kpsantiago.caca_oinbu.exception.ConflictException;
import io.github.kpsantiago.caca_oinbu.exception.NotFoundException;
import io.github.kpsantiago.caca_oinbu.mapper.UserMapper;
import io.github.kpsantiago.caca_oinbu.repository.UserRepository;
import io.github.kpsantiago.caca_oinbu.service.contract.IAuthService;
import io.github.kpsantiago.caca_oinbu.service.contract.ITokenService;
import io.github.kpsantiago.caca_oinbu.service.contract.IUserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService implements IUserService, IAuthService, UserDetailsService {

    private final UserRepository repository;

    private final UserMapper mapper;

    private final PasswordEncoder encoder;

    private final AuthenticationConfiguration authenticationConfiguration;

    private final ITokenService tokenService;

    @Override
    @Transactional
    public UserResponseDto create(UserRequestDto request) {
        var userAlreadyExists = repository.findByEmailIgnoreCase(request.getEmail()).isPresent();
        if(userAlreadyExists)
            throw new ConflictException("Um usuário com o email " + request.getEmail() + " já existe");

        request.setPassword(encoder.encode(request.getPassword()));

        var entity = mapper.toEntity(request);
        entity.setRole(Role.USER);

        return mapper.toDto(repository.save(entity));
    }

    @Override
    public TokenResponse login(LoginRequestDto request) {
        AuthenticationManager manager = authenticationConfiguration.getAuthenticationManager();
        Authentication authentication = mapper.toAuthentication(request);
        Authentication authenticated = manager.authenticate(authentication);

        return tokenService.generateToken(authenticated);
    }

    @Override
    public String getUserFromToken(String token) {
        return tokenService.getUserFromToken(token);
    }

    @Override
    public boolean isTokenValid(String token) {
        return tokenService.isTokenValid(token);
    }

    @Override
    public UserResponseDto profile(String userEmail) {
        return mapper.toDto(repository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado")));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return repository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário ou senha inválidos"));
    }
}
