package io.github.kpsantiago.caca_oinbu.controller.contract;

import io.github.kpsantiago.caca_oinbu.dto.request.BusRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.BusResponseDto;
import io.github.kpsantiago.caca_oinbu.enums.BusSort;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

public interface IBusController {
    @Operation(summary = "Adiciona um novo ônibus", description = "Adiciona um novo ônibus")
    ResponseEntity<BusResponseDto> create(BusRequestDto request);

    @Operation(summary = "Busca um ônibus por parâmetro", description = "Busca um ônibus por parâmetro")
    ResponseEntity<BusResponseDto> getBusByParam(BusSort param, String value);

    @Operation(summary = "Busca todos os ônibus", description = "Busca todos os ônibus")
    ResponseEntity<Page<BusResponseDto>> getAllBuses(Integer page, Integer size, BusSort sort, Sort.Direction direction);

    @Operation(summary = "Atualiza um ônibus", description = "Atualiza um ônibus")
    ResponseEntity<BusResponseDto> updateBus(BusRequestDto request, String id);
}
