package io.github.kpsantiago.caca_oinbu.service.contract;

import io.github.kpsantiago.caca_oinbu.dto.request.UserRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.UserResponseDto;

public interface IUserService {
    UserResponseDto create(UserRequestDto request);
}
