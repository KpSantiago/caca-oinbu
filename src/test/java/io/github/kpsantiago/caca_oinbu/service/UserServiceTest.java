package io.github.kpsantiago.caca_oinbu.service;

import io.github.kpsantiago.caca_oinbu.dto.request.LoginRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.request.UserRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.TokenResponse;
import io.github.kpsantiago.caca_oinbu.dto.response.UserResponseDto;
import io.github.kpsantiago.caca_oinbu.enums.Role;
import io.github.kpsantiago.caca_oinbu.exception.BadJwtException;
import io.github.kpsantiago.caca_oinbu.exception.ConflictException;
import io.github.kpsantiago.caca_oinbu.exception.ForbiddenException;
import io.github.kpsantiago.caca_oinbu.exception.NotFoundException;
import io.github.kpsantiago.caca_oinbu.mapper.UserMapper;
import io.github.kpsantiago.caca_oinbu.model.User;
import io.github.kpsantiago.caca_oinbu.repository.UserRepository;
import io.github.kpsantiago.caca_oinbu.service.contract.ITokenService;
import io.github.kpsantiago.caca_oinbu.validation.UserValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @Mock
    private UserValidation validation;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private ITokenService tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService service;

    @Test
    void shouldCreateUser() {
        var request = new UserRequestDto();
        request.setName("John Doe");
        request.setEmail("john@email.com");
        request.setPassword("password123");

        var user = new User();
        user.setId("user-1");
        user.setName("John Doe");
        user.setEmail("john@email.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);

        var responseDto = new UserResponseDto();
        responseDto.setId("user-1");
        responseDto.setName("John Doe");
        responseDto.setEmail("john@email.com");

        when(repository.findByEmailIgnoreCase("john@email.com")).thenReturn(Optional.empty());
        doNothing().when(validation).validateUserDoesNotExist(Optional.empty());
        when(encoder.encode("password123")).thenReturn("encodedPassword");
        when(mapper.toEntity(request)).thenReturn(user);
        when(repository.save(user)).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(responseDto);

        var result = service.create(request);

        assertNotNull(result);
        assertEquals("user-1", result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@email.com", result.getEmail());

        verify(repository).findByEmailIgnoreCase("john@email.com");
        verify(validation).validateUserDoesNotExist(Optional.empty());
        verify(encoder).encode("password123");
        verify(mapper).toEntity(request);
        verify(repository).save(user);
        verify(mapper).toDto(user);
    }

    @Test
    void shouldThrowWhenCreateUserWithExistingEmail() {
        var request = new UserRequestDto();
        request.setName("John Doe");
        request.setEmail("john@email.com");
        request.setPassword("password123");

        var existingUser = new User();
        existingUser.setId("existing-user");
        existingUser.setEmail("john@email.com");

        var exception = new ConflictException("User already exists");

        when(repository.findByEmailIgnoreCase("john@email.com")).thenReturn(Optional.of(existingUser));
        doThrow(exception).when(validation).validateUserDoesNotExist(Optional.of(existingUser));

        assertThrows(ConflictException.class, () -> service.create(request), exception.getMessage());

        verify(repository).findByEmailIgnoreCase("john@email.com");
        verify(validation).validateUserDoesNotExist(Optional.of(existingUser));
        verifyNoMoreInteractions(encoder, mapper, repository);
    }

    @Test
    void shouldLogin() {
        var request = new LoginRequestDto();
        request.setEmail("john@email.com");
        request.setPassword("password123");

        var user = new User();
        user.setId("user-1");
        user.setEmail("john@email.com");
        user.setRole(Role.USER);

        var auth = mock(Authentication.class);
        var tokenResponse = new TokenResponse("token-value", Role.USER, 30);

        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
        when(mapper.toAuthentication(request)).thenReturn(auth);
        when(authenticationManager.authenticate(auth)).thenReturn(authentication);
        when(tokenService.generateToken(authentication)).thenReturn(tokenResponse);

        var result = service.login(request);

        assertNotNull(result);
        assertEquals("token-value", result.getToken());
        assertEquals(Role.USER, result.getRole());
        assertEquals(30, result.getExpiration());

        verify(authenticationConfiguration).getAuthenticationManager();
        verify(mapper).toAuthentication(request);
        verify(authenticationManager).authenticate(auth);
        verify(tokenService).generateToken(authentication);
    }

    @Test
    void shouldGetUserFromToken() {
        var token = "valid-token";
        var userEmail = "john@email.com";

        when(tokenService.getUserFromToken(token)).thenReturn(userEmail);

        var result = service.getUserFromToken(token);

        assertEquals(userEmail, result);

        verify(tokenService).getUserFromToken(token);
    }

    @Test
    void shouldThrowWhenGetUserFromTokenWithInvalidToken() {
        var token = "invalid-token";
        var exception = new BadJwtException("Token inválido");

        when(tokenService.getUserFromToken(token)).thenThrow(exception);

        assertThrows(BadJwtException.class, () -> service.getUserFromToken(token), exception.getMessage());

        verify(tokenService).getUserFromToken(token);
    }

    @Test
    void shouldValidateToken() {
        var token = "valid-token";

        when(tokenService.isTokenValid(token)).thenReturn(true);

        var result = service.isTokenValid(token);

        assertTrue(result);

        verify(tokenService).isTokenValid(token);
    }

    @Test
    void shouldThrowWhenValidateTokenWithInvalidToken() {
        var token = "invalid-token";
        var exception = new BadJwtException("Token inválido");

        when(tokenService.isTokenValid(token)).thenThrow(exception);

        assertThrows(BadJwtException.class, () -> service.isTokenValid(token), exception.getMessage());

        verify(tokenService).isTokenValid(token);
    }

    @Test
    void shouldGetProfile() {
        var userEmail = "john@email.com";

        var user = new User();
        user.setId("user-1");
        user.setName("John Doe");
        user.setEmail("john@email.com");

        var responseDto = new UserResponseDto();
        responseDto.setId("user-1");
        responseDto.setName("John Doe");
        responseDto.setEmail("john@email.com");

        when(repository.findByEmailIgnoreCase(userEmail)).thenReturn(Optional.of(user));
        when(validation.validateUserExists(Optional.of(user))).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(responseDto);

        var result = service.profile(userEmail);

        assertNotNull(result);
        assertEquals("user-1", result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@email.com", result.getEmail());

        verify(repository).findByEmailIgnoreCase(userEmail);
        verify(validation).validateUserExists(Optional.of(user));
        verify(mapper).toDto(user);
    }

    @Test
    void shouldThrowWhenGetProfileWithNonExistentUser() {
        var userEmail = "nonexistent@email.com";
        var exception = new NotFoundException("User not found");

        when(repository.findByEmailIgnoreCase(userEmail)).thenReturn(Optional.empty());
        when(validation.validateUserExists(Optional.empty())).thenThrow(exception);

        assertThrows(NotFoundException.class, () -> service.profile(userEmail), exception.getMessage());

        verify(repository).findByEmailIgnoreCase(userEmail);
        verify(validation).validateUserExists(Optional.empty());
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void shouldLoadUserByUsername() {
        var username = "john@email.com";

        var user = new User();
        user.setId("user-1");
        user.setEmail("john@email.com");

        when(repository.findByEmailIgnoreCase(username)).thenReturn(Optional.of(user));

        var result = service.loadUserByUsername(username);

        assertNotNull(result);
        assertEquals("john@email.com", ((User) result).getEmail());

        verify(repository).findByEmailIgnoreCase(username);
    }

    @Test
    void shouldThrowWhenLoadUserByUsernameWithNonExistentUser() {
        var username = "nonexistent@email.com";

        when(repository.findByEmailIgnoreCase(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(username));

        verify(repository).findByEmailIgnoreCase(username);
    }
}
