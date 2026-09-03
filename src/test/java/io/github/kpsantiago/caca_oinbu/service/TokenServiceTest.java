package io.github.kpsantiago.caca_oinbu.service;

import io.github.kpsantiago.caca_oinbu.dto.response.TokenResponse;
import io.github.kpsantiago.caca_oinbu.enums.Role;
import io.github.kpsantiago.caca_oinbu.exception.BadJwtException;
import io.github.kpsantiago.caca_oinbu.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private JwtEncoder encoder;

    @Mock
    private JwtDecoder decoder;

    @Mock
    private Jwt jwt;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TokenService service;

    @BeforeEach
    void setup() {
        service.setExpiration(30);
    }

    @Test
    void shouldGenerateToken() {
        var user = new User();
        user.setId("user-1");
        user.setEmail("john@email.com");
        user.setRole(Role.USER);

        var encodedJwt = mock(Jwt.class);
        when(encodedJwt.getTokenValue()).thenReturn("encoded-token-value");

        when(authentication.getPrincipal()).thenReturn(user);
        when(authentication.getAuthorities()).thenReturn((Collection) List.of(Role.USER));
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(encodedJwt);

        var result = service.generateToken(authentication);

        assertNotNull(result);
        assertEquals("encoded-token-value", result.getToken());
        assertEquals(Role.USER, result.getRole());

        verify(encoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void shouldGenerateTokenWithDriverRole() {
        var user = new User();
        user.setId("user-1");
        user.setEmail("driver@email.com");
        user.setRole(Role.DRIVER);

        var authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(Role.DRIVER.getAuthority()));

        var encodedJwt = mock(Jwt.class);
        when(encodedJwt.getTokenValue()).thenReturn("encoded-token-value");

        when(authentication.getPrincipal()).thenReturn(user);
        when(authentication.getAuthorities()).thenReturn((Collection) List.of(Role.DRIVER));
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(encodedJwt);

        var result = service.generateToken(authentication);

        assertNotNull(result);
        assertEquals("encoded-token-value", result.getToken());
        assertEquals(Role.DRIVER, result.getRole());

        verify(encoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void shouldGenerateTokenWithAdminRole() {
        var user = new User();
        user.setId("user-1");
        user.setEmail("admin@email.com");
        user.setRole(Role.ADMIN);

        var encodedJwt = mock(Jwt.class);
        when(encodedJwt.getTokenValue()).thenReturn("encoded-token-value");

        when(authentication.getPrincipal()).thenReturn(user);
        when(authentication.getAuthorities()).thenReturn((Collection) List.of(Role.ADMIN));
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(encodedJwt);

        var result = service.generateToken(authentication);

        assertNotNull(result);
        assertEquals("encoded-token-value", result.getToken());
        assertEquals(Role.ADMIN, result.getRole());

        verify(encoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void shouldGetUserFromToken() {
        var token = "valid-token";
        var userEmail = "john@email.com";

        when(decoder.decode(token)).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn(userEmail);

        var result = service.getUserFromToken(token);

        assertEquals(userEmail, result);

        verify(decoder).decode(token);
        verify(jwt).getSubject();
    }

    @Test
    void shouldThrowWhenGetUserFromTokenWithInvalidToken() {
        var token = "invalid-token";
        var exception = new BadJwtException("Token inválido");

        when(decoder.decode(token)).thenThrow(new BadJwtException("Invalid token"));

        assertThrows(BadJwtException.class, () -> service.getUserFromToken(token), exception.getMessage());

        verify(decoder).decode(token);
    }

    @Test
    void shouldValidateToken() {
        var token = "valid-token";

        when(decoder.decode(token)).thenReturn(jwt);

        var result = service.isTokenValid(token);

        assertTrue(result);

        verify(decoder).decode(token);
    }

    @Test
    void shouldThrowWhenValidateTokenWithInvalidToken() {
        var token = "invalid-token";
        var exception = new BadJwtException("Token inválido");

        when(decoder.decode(token)).thenThrow(new RuntimeException("Invalid token"));

        assertThrows(BadJwtException.class, () -> service.isTokenValid(token), exception.getMessage());

        verify(decoder).decode(token);
    }

    @Test
    void shouldThrowWhenValidateTokenWithExpiredToken() {
        var token = "expired-token";
        var exception = new BadJwtException("Token inválido");

        when(decoder.decode(token)).thenThrow(new RuntimeException("Token expired"));

        assertThrows(BadJwtException.class, () -> service.isTokenValid(token), exception.getMessage());

        verify(decoder).decode(token);
    }

    @Test
    void shouldThrowWhenValidateTokenWithMalformedToken() {
        var token = "malformed-token";
        var exception = new BadJwtException("Token inválido");

        when(decoder.decode(token)).thenThrow(new RuntimeException("Malformed token"));

        assertThrows(BadJwtException.class, () -> service.isTokenValid(token), exception.getMessage());

        verify(decoder).decode(token);
    }
}
