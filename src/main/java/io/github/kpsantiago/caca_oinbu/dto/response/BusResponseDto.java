package io.github.kpsantiago.caca_oinbu.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusResponseDto {
    private String id;
    private String name;
    private String plate;
    private UserResponseDto driver;
}
