package io.github.kpsantiago.caca_oinbu.controller;

import io.github.kpsantiago.caca_oinbu.config.ApiConfig;
import io.github.kpsantiago.caca_oinbu.controller.contract.IBusController;
import io.github.kpsantiago.caca_oinbu.dto.request.BusRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.BusResponseDto;
import io.github.kpsantiago.caca_oinbu.enums.BusSort;
import io.github.kpsantiago.caca_oinbu.service.contract.IBusService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConfig.BUS_BASE_PATH)
@AllArgsConstructor
public class BusController implements IBusController {
    private final IBusService service;

    @Override
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BusResponseDto> create(@RequestBody BusRequestDto request) {
        return ResponseEntity.ok(service.createBus(request));
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAnyAuthority('DRIVER', 'ADMIN')")
    public ResponseEntity<BusResponseDto> getBusByParam(@RequestParam BusSort param, @RequestParam String value) {
        return ResponseEntity.ok(service.getBusByParam(param, value));
    }
}
