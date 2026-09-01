package io.github.kpsantiago.caca_oinbu.service.contract;

import io.github.kpsantiago.caca_oinbu.dto.request.BusRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.BusResponseDto;
import io.github.kpsantiago.caca_oinbu.enums.BusSort;

import java.util.List;

public interface IBusService {
    BusResponseDto createBus(BusRequestDto request);
    BusResponseDto getBusByParam(BusSort param, String value);
    List<BusResponseDto> getAllBuses();
    BusResponseDto updateBus(BusRequestDto request, String id);
}
