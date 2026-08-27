package io.github.kpsantiago.caca_oinbu.mapper;

import io.github.kpsantiago.caca_oinbu.dto.request.LoginRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.request.UserRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.UserResponseDto;
import io.github.kpsantiago.caca_oinbu.model.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;

    public User toEntity(UserRequestDto request) {
        return modelMapper.map(request, User.class);
    }

    public Authentication toAuthentication(LoginRequestDto request) {
        return new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
    }

    public UserResponseDto toDto(User entity) {
        return modelMapper.map(entity, UserResponseDto.class);
    }
}
