package io.github.kpsantiago.caca_oinbu.service;

import io.github.kpsantiago.caca_oinbu.dto.request.BusRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.BusResponseDto;
import io.github.kpsantiago.caca_oinbu.enums.BusSort;
import io.github.kpsantiago.caca_oinbu.mapper.BusMapper;
import io.github.kpsantiago.caca_oinbu.repository.BusRepository;
import io.github.kpsantiago.caca_oinbu.repository.UserRepository;
import io.github.kpsantiago.caca_oinbu.service.contract.IBusService;
import io.github.kpsantiago.caca_oinbu.validation.BusValidation;
import io.github.kpsantiago.caca_oinbu.validation.UserValidation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.github.kpsantiago.caca_oinbu.enums.Role.*;

@Service
@AllArgsConstructor
public class BusService implements IBusService {
    private final BusRepository repository;
    private final UserRepository userRepository;

    private final BusMapper mapper;

    private final BusValidation validation;
    private final UserValidation userValidation;

    @Override
    public BusResponseDto createBus(BusRequestDto request, String requesterEmail) {
        var admin = userValidation.validateUserExists(userRepository.findByEmailIgnoreCase(requesterEmail));
        userValidation.validateUserIs(admin, ADMIN);

        validation.validateBusDoesNotExist(repository.findByPlateIgnoreCase(request.getPlate()));

        var driver = userValidation.validateUserExists(userRepository.findById(request.getDriverId()));
        userValidation.validateUserIs(driver, DRIVER);

        var bus = mapper.toEntity(request);
        bus.setDriver(driver);

        return mapper.toDto(repository.save(bus));
    }

    @Override
    public BusResponseDto getBusByParam(BusSort param, String value, String requesterEmail) {
        var admin = userValidation.validateUserExists(userRepository.findByEmailIgnoreCase(requesterEmail));
        userValidation.validateUserIs(admin, ADMIN);

        var bus = validation.validateBusExists(repository.findByParam(param.name(), value));

        return mapper.toDto(bus);
    }

    @Override
    public Page<BusResponseDto> getAllBuses(Pageable pageable, String requesterEmail) {
        var admin = userValidation.validateUserExists(userRepository.findByEmailIgnoreCase(requesterEmail));
        userValidation.validateUserIs(admin, ADMIN);

        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public BusResponseDto updateBus(BusRequestDto request, String id, String requesterEmail) {
        var admin = userValidation.validateUserExists(userRepository.findByEmailIgnoreCase(requesterEmail));
        userValidation.validateUserIs(admin, ADMIN);

        return null;
    }
}
