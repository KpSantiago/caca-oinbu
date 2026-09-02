package io.github.kpsantiago.caca_oinbu.controller;

import io.github.kpsantiago.caca_oinbu.config.ApiConfig;
import io.github.kpsantiago.caca_oinbu.controller.contract.IBusController;
import io.github.kpsantiago.caca_oinbu.dto.request.BusRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.BusResponseDto;
import io.github.kpsantiago.caca_oinbu.enums.BusSort;
import io.github.kpsantiago.caca_oinbu.model.User;
import io.github.kpsantiago.caca_oinbu.service.contract.IBusService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConfig.BUS_BASE_PATH)
@AllArgsConstructor
public class BusController implements IBusController {
    private final IBusService service;

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BusResponseDto> create(@RequestBody BusRequestDto request) {
        User requester = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(service.createBus(request, requester.getEmail()));
    }

    @Override
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    public ResponseEntity<BusResponseDto> getBusByParam(@RequestParam BusSort param, @RequestParam String value) {
        User requester = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(service.getBusByParam(param, value, requester.getEmail()));
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<BusResponseDto>> getAllBuses(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "ID") BusSort sort,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        User requester = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort.name()));

        return ResponseEntity.ok(service.getAllBuses(pageable, requester.getEmail()));
    }

    @Override
    @PutMapping
    @PreAuthorize("hasRole('DRIVER', 'ADMIN')")
    public ResponseEntity<BusResponseDto> updateBus(@RequestBody BusRequestDto request, @PathVariable String id) {
        User requester = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(service.updateBus(request, id, requester.getEmail()));
    }
}
