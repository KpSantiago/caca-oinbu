package io.github.kpsantiago.caca_oinbu.service.contract;

import io.github.kpsantiago.caca_oinbu.dto.request.BusRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.BusResponseDto;
import io.github.kpsantiago.caca_oinbu.enums.BusSort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBusService {
    BusResponseDto createBus(BusRequestDto request, String requesterEmail);
    BusResponseDto getBusByParam(BusSort param, String value, String requesterEmail);
    Page<BusResponseDto> getAllBuses(Pageable pageable, String requesterEmail);
    BusResponseDto updateBus(BusRequestDto request, String id, String requesterEmail);
}
