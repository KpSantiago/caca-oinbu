package io.github.kpsantiago.caca_oinbu.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusRequestDto {
    private String name;
    private String plate;
    private String driverId;
}
