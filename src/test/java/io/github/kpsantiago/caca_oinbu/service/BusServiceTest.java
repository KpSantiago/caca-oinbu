package io.github.kpsantiago.caca_oinbu.service;

import io.github.kpsantiago.caca_oinbu.dto.request.BusRequestDto;
import io.github.kpsantiago.caca_oinbu.dto.response.BusResponseDto;
import io.github.kpsantiago.caca_oinbu.enums.Role;
import io.github.kpsantiago.caca_oinbu.exception.ConflictException;
import io.github.kpsantiago.caca_oinbu.exception.ForbiddenException;
import io.github.kpsantiago.caca_oinbu.exception.NotFoundException;
import io.github.kpsantiago.caca_oinbu.mapper.BusMapper;
import io.github.kpsantiago.caca_oinbu.model.Bus;
import io.github.kpsantiago.caca_oinbu.model.User;
import io.github.kpsantiago.caca_oinbu.repository.BusRepository;
import io.github.kpsantiago.caca_oinbu.repository.UserRepository;
import io.github.kpsantiago.caca_oinbu.validation.BusValidation;
import io.github.kpsantiago.caca_oinbu.validation.UserValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import io.github.kpsantiago.caca_oinbu.enums.BusSort;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {
    @Mock
    private BusRepository repository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private BusMapper mapper;

    @Mock
    private BusValidation validation;
    @Mock
    private UserValidation userValidation;

    @InjectMocks
    private BusService service;

    @Test
    void shouldCreateBus() {
        var request = new BusRequestDto();
        request.setName("Bus 1");
        request.setPlate("ABC-123");
        request.setDriverId("1");

        var driver = new User();
        driver.setId("1");
        driver.setRole(Role.DRIVER);

        var admin = new User();
        admin.setId("2");
        admin.setEmail("test@email.com");
        admin.setRole(Role.ADMIN);

        var bus = new Bus();
        bus.setId("bus-1");
        bus.setName("Bus 1");
        bus.setPlate("ABC-123");
        bus.setDriver(driver);

        var responseDto = new BusResponseDto();
        responseDto.setId("bus-1");
        responseDto.setName("Bus 1");
        responseDto.setPlate("ABC-123");

        when(userRepository.findByEmailIgnoreCase("test@email.com")).thenReturn(Optional.of(admin));
        when(userValidation.validateUserExists(Optional.of(admin))).thenReturn(admin);
        doNothing().when(userValidation).validateUserIs(admin, Role.ADMIN);

        when(repository.findByPlateIgnoreCase("ABC-123")).thenReturn(Optional.empty());
        doNothing().when(validation).validateBusDoesNotExist(Optional.empty());

        when(userRepository.findById("1")).thenReturn(Optional.of(driver));
        when(userValidation.validateUserExists(Optional.of(driver)))
                .thenReturn(driver);
        doNothing().when(userValidation).validateUserIs(driver, Role.DRIVER);

        when(mapper.toEntity(request)).thenReturn(bus);
        when(repository.save(bus)).thenReturn(bus);
        when(mapper.toDto(bus)).thenReturn(responseDto);

        var result = service.createBus(request, admin.getEmail());

        assertNotNull(result);
        assertEquals("bus-1", result.getId());
        assertEquals("Bus 1", result.getName());
        assertEquals("ABC-123", result.getPlate());

        verify(userRepository).findByEmailIgnoreCase("test@email.com");
        verify(userValidation).validateUserExists(Optional.of(admin));
        verify(userValidation).validateUserIs(admin, Role.ADMIN);

        verify(repository).findByPlateIgnoreCase("ABC-123");
        verify(validation).validateBusDoesNotExist(Optional.empty());

        verify(userRepository).findById("1");
        verify(userValidation).validateUserExists(Optional.of(driver));
        verify(userValidation).validateUserIs(driver, Role.DRIVER);

        verify(mapper).toEntity(request);
        verify(repository).save(bus);
        verify(mapper).toDto(bus);
    }

    @Test
    void shouldThrowWhenCreateBusWithNonExistentRequester() {
        var request = new BusRequestDto();
        request.setName("Bus 1");
        request.setPlate("ABC-123");
        request.setDriverId("1");

        var exception = new NotFoundException("not found");

        when(userRepository.findByEmailIgnoreCase("nonexistent@email.com")).thenReturn(Optional.empty());
        when(userValidation.validateUserExists(Optional.empty())).thenThrow(exception);

        assertThrows(NotFoundException.class, () -> service.createBus(request, "nonexistent@email.com"), exception.getMessage());

        verify(userRepository).findByEmailIgnoreCase("nonexistent@email.com");
        verify(userValidation).validateUserExists(Optional.empty());
        verifyNoMoreInteractions(userValidation, repository, mapper);
    }

    @Test
    void shouldThrowWhenCreateBusWithNonAdminRequester() {
        var request = new BusRequestDto();
        request.setName("Bus 1");
        request.setPlate("ABC-123");
        request.setDriverId("1");

        var driver = new User();
        driver.setId("1");
        driver.setRole(Role.DRIVER);

        when(userRepository.findByEmailIgnoreCase("driver@email.com")).thenReturn(Optional.of(driver));
        when(userValidation.validateUserExists(Optional.of(driver))).thenReturn(driver);
        doThrow(new ForbiddenException("User is not admin")).when(userValidation).validateUserIs(driver, Role.ADMIN);

        assertThrows(ForbiddenException.class, () -> service.createBus(request, "driver@email.com"));

        verify(userRepository).findByEmailIgnoreCase("driver@email.com");
        verify(userValidation).validateUserExists(Optional.of(driver));
        verify(userValidation).validateUserIs(driver, Role.ADMIN);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldThrowWhenCreateBusWithExistingPlate() {
        var request = new BusRequestDto();
        request.setName("Bus 1");
        request.setPlate("ABC-123");
        request.setDriverId("1");

        var admin = new User();
        admin.setId("2");
        admin.setEmail("test@email.com");
        admin.setRole(Role.ADMIN);

        var existingBus = new Bus();
        existingBus.setId("existing-bus");
        existingBus.setPlate("ABC-123");

        when(userRepository.findByEmailIgnoreCase("test@email.com")).thenReturn(Optional.of(admin));
        when(userValidation.validateUserExists(Optional.of(admin))).thenReturn(admin);
        doNothing().when(userValidation).validateUserIs(admin, Role.ADMIN);

        when(repository.findByPlateIgnoreCase("ABC-123")).thenReturn(Optional.of(existingBus));
        doThrow(new ConflictException("Bus already exists")).when(validation).validateBusDoesNotExist(Optional.of(existingBus));

        assertThrows(ConflictException.class, () -> service.createBus(request, "test@email.com"));

        verify(userRepository).findByEmailIgnoreCase("test@email.com");
        verify(userValidation).validateUserExists(Optional.of(admin));
        verify(userValidation).validateUserIs(admin, Role.ADMIN);
        verify(repository).findByPlateIgnoreCase("ABC-123");
        verify(validation).validateBusDoesNotExist(Optional.of(existingBus));
        verifyNoMoreInteractions(userRepository, mapper);
    }

    @Test
    void shouldThrowWhenCreateBusWithNonExistentDriver() {
        var request = new BusRequestDto();
        request.setName("Bus 1");
        request.setPlate("ABC-123");
        request.setDriverId("nonexistent-driver");

        var admin = new User();
        admin.setId("2");
        admin.setEmail("test@email.com");
        admin.setRole(Role.ADMIN);

        var bus = new Bus();
        bus.setId("bus-1");
        bus.setName("Bus 1");
        bus.setPlate("ABC-123");

        var exception = new NotFoundException("not found");

        when(userRepository.findByEmailIgnoreCase("test@email.com")).thenReturn(Optional.of(admin));
        when(userValidation.validateUserExists(Optional.of(admin))).thenReturn(admin);
        doNothing().when(userValidation).validateUserIs(admin, Role.ADMIN);

        when(repository.findByPlateIgnoreCase("ABC-123")).thenReturn(Optional.empty());
        doNothing().when(validation).validateBusDoesNotExist(Optional.empty());

        when(userRepository.findById("nonexistent-driver")).thenReturn(Optional.empty());
        when(userValidation.validateUserExists(Optional.empty())).thenThrow(exception);

        assertThrows(NotFoundException.class, () -> service.createBus(request, "test@email.com"), exception.getMessage());

        verify(userRepository).findByEmailIgnoreCase("test@email.com");
        verify(userValidation).validateUserExists(Optional.of(admin));
        verify(userValidation).validateUserIs(admin, Role.ADMIN);
        verify(repository).findByPlateIgnoreCase("ABC-123");
        verify(validation).validateBusDoesNotExist(Optional.empty());
        verify(userRepository).findById("nonexistent-driver");
        verify(userValidation).validateUserExists(Optional.empty());
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void shouldThrowWhenCreateBusWithNonDriverRole() {
        var request = new BusRequestDto();
        request.setName("Bus 1");
        request.setPlate("ABC-123");
        request.setDriverId("1");

        var admin = new User();
        admin.setId("2");
        admin.setEmail("test@email.com");
        admin.setRole(Role.ADMIN);

        var passenger = new User();
        passenger.setId("1");
        passenger.setRole(Role.USER);

        var bus = new Bus();
        bus.setId("bus-1");
        bus.setName("Bus 1");
        bus.setPlate("ABC-123");

        when(userRepository.findByEmailIgnoreCase("test@email.com")).thenReturn(Optional.of(admin));
        when(userValidation.validateUserExists(Optional.of(admin))).thenReturn(admin);
        doNothing().when(userValidation).validateUserIs(admin, Role.ADMIN);

        when(repository.findByPlateIgnoreCase("ABC-123")).thenReturn(Optional.empty());
        doNothing().when(validation).validateBusDoesNotExist(Optional.empty());

        when(userRepository.findById("1")).thenReturn(Optional.of(passenger));
        when(userValidation.validateUserExists(Optional.of(passenger))).thenReturn(passenger);
        doThrow(new ForbiddenException("User is not driver")).when(userValidation).validateUserIs(passenger, Role.DRIVER);

        assertThrows(ForbiddenException.class, () -> service.createBus(request, "test@email.com"));

        verify(userRepository).findByEmailIgnoreCase("test@email.com");
        verify(userValidation).validateUserExists(Optional.of(admin));
        verify(userValidation).validateUserIs(admin, Role.ADMIN);
        verify(repository).findByPlateIgnoreCase("ABC-123");
        verify(validation).validateBusDoesNotExist(Optional.empty());
        verify(userRepository).findById("1");
        verify(userValidation).validateUserExists(Optional.of(passenger));
        verify(userValidation).validateUserIs(passenger, Role.DRIVER);
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void shouldGetBusById() {
        var admin = new User();
        admin.setId("2");
        admin.setEmail("test@email.com");
        admin.setRole(Role.ADMIN);

        var driver = new User();
        driver.setId("1");
        driver.setRole(Role.DRIVER);

        var bus = new Bus();
        bus.setId("bus-1");
        bus.setName("Bus 1");
        bus.setPlate("ABC-123");
        bus.setDriver(driver);

        var responseDto = new BusResponseDto();
        responseDto.setId("bus-1");
        responseDto.setName("Bus 1");
        responseDto.setPlate("ABC-123");

        when(userRepository.findByEmailIgnoreCase("test@email.com")).thenReturn(Optional.of(admin));
        when(userValidation.validateUserExists(Optional.of(admin))).thenReturn(admin);
        doNothing().when(userValidation).validateUserIs(admin, Role.ADMIN);

        when(repository.findByParam("ID", "bus-1")).thenReturn(Optional.of(bus));
        when(validation.validateBusExists(Optional.of(bus))).thenReturn(bus);
        when(mapper.toDto(bus)).thenReturn(responseDto);

        var result = service.getBusByParam(BusSort.ID, "bus-1", "test@email.com");

        assertNotNull(result);
        assertEquals("bus-1", result.getId());
        assertEquals("Bus 1", result.getName());
        assertEquals("ABC-123", result.getPlate());

        verify(userRepository).findByEmailIgnoreCase("test@email.com");
        verify(userValidation).validateUserExists(Optional.of(admin));
        verify(userValidation).validateUserIs(admin, Role.ADMIN);
        verify(repository).findByParam("ID", "bus-1");
        verify(validation).validateBusExists(Optional.of(bus));
        verify(mapper).toDto(bus);
    }

    @Test
    void shouldGetBusByName() {
        var admin = new User();
        admin.setId("2");
        admin.setEmail("test@email.com");
        admin.setRole(Role.ADMIN);

        var bus = new Bus();
        bus.setId("bus-1");
        bus.setName("Bus 1");

        var responseDto = new BusResponseDto();
        responseDto.setId("bus-1");
        responseDto.setName("Bus 1");

        when(userRepository.findByEmailIgnoreCase("test@email.com")).thenReturn(Optional.of(admin));
        when(userValidation.validateUserExists(Optional.of(admin))).thenReturn(admin);
        doNothing().when(userValidation).validateUserIs(admin, Role.ADMIN);

        when(repository.findByParam("NAME", "Bus 1")).thenReturn(Optional.of(bus));
        when(validation.validateBusExists(Optional.of(bus))).thenReturn(bus);
        when(mapper.toDto(bus)).thenReturn(responseDto);

        var result = service.getBusByParam(BusSort.NAME, "Bus 1", "test@email.com");

        assertNotNull(result);
        assertEquals("bus-1", result.getId());
        assertEquals("Bus 1", result.getName());

        verify(userRepository).findByEmailIgnoreCase("test@email.com");
        verify(userValidation).validateUserExists(Optional.of(admin));
        verify(userValidation).validateUserIs(admin, Role.ADMIN);
        verify(repository).findByParam("NAME", "Bus 1");
        verify(validation).validateBusExists(Optional.of(bus));
        verify(mapper).toDto(bus);
    }

    @Test
    void shouldGetBusByPlate() {
        var admin = new User();
        admin.setId("2");
        admin.setEmail("test@email.com");
        admin.setRole(Role.ADMIN);

        var bus = new Bus();
        bus.setId("bus-1");
        bus.setPlate("ABC-123");

        var responseDto = new BusResponseDto();
        responseDto.setId("bus-1");
        responseDto.setPlate("ABC-123");

        when(userRepository.findByEmailIgnoreCase("test@email.com")).thenReturn(Optional.of(admin));
        when(userValidation.validateUserExists(Optional.of(admin))).thenReturn(admin);
        doNothing().when(userValidation).validateUserIs(admin, Role.ADMIN);

        when(repository.findByParam("PLATE", "ABC-123")).thenReturn(Optional.of(bus));
        when(validation.validateBusExists(Optional.of(bus))).thenReturn(bus);
        when(mapper.toDto(bus)).thenReturn(responseDto);

        var result = service.getBusByParam(BusSort.PLATE, "ABC-123", "test@email.com");

        assertNotNull(result);
        assertEquals("bus-1", result.getId());
        assertEquals("ABC-123", result.getPlate());

        verify(userRepository).findByEmailIgnoreCase("test@email.com");
        verify(userValidation).validateUserExists(Optional.of(admin));
        verify(userValidation).validateUserIs(admin, Role.ADMIN);
        verify(repository).findByParam("PLATE", "ABC-123");
        verify(validation).validateBusExists(Optional.of(bus));
        verify(mapper).toDto(bus);
    }

    @Test
    void shouldGetBusByDriver() {
        var admin = new User();
        admin.setId("2");
        admin.setEmail("test@email.com");
        admin.setRole(Role.ADMIN);

        var bus = new Bus();
        bus.setId("bus-1");

        var responseDto = new BusResponseDto();
        responseDto.setId("bus-1");

        when(userRepository.findByEmailIgnoreCase("test@email.com")).thenReturn(Optional.of(admin));
        when(userValidation.validateUserExists(Optional.of(admin))).thenReturn(admin);
        doNothing().when(userValidation).validateUserIs(admin, Role.ADMIN);

        when(repository.findByParam("DRIVER", "John Doe")).thenReturn(Optional.of(bus));
        when(validation.validateBusExists(Optional.of(bus))).thenReturn(bus);
        when(mapper.toDto(bus)).thenReturn(responseDto);

        var result = service.getBusByParam(BusSort.DRIVER, "John Doe", "test@email.com");

        assertNotNull(result);
        assertEquals("bus-1", result.getId());

        verify(userRepository).findByEmailIgnoreCase("test@email.com");
        verify(userValidation).validateUserExists(Optional.of(admin));
        verify(userValidation).validateUserIs(admin, Role.ADMIN);
        verify(repository).findByParam("DRIVER", "John Doe");
        verify(validation).validateBusExists(Optional.of(bus));
        verify(mapper).toDto(bus);
    }

    @Test
    void shouldThrowWhenGetBusByParamWithNonExistentRequester() {
        var exception = new NotFoundException("not found");

        when(userRepository.findByEmailIgnoreCase("nonexistent@email.com")).thenReturn(Optional.empty());
        when(userValidation.validateUserExists(Optional.empty())).thenThrow(exception);

        assertThrows(NotFoundException.class, () -> service.getBusByParam(BusSort.ID, "bus-1", "nonexistent@email.com"), exception.getMessage());

        verify(userRepository).findByEmailIgnoreCase("nonexistent@email.com");
        verify(userValidation).validateUserExists(Optional.empty());
        verifyNoMoreInteractions(userValidation, repository, mapper);
    }

    @Test
    void shouldThrowWhenGetBusByParamWithNonAdminRequester() {
        var driver = new User();
        driver.setId("1");
        driver.setRole(Role.DRIVER);

        when(userRepository.findByEmailIgnoreCase("driver@email.com")).thenReturn(Optional.of(driver));
        when(userValidation.validateUserExists(Optional.of(driver))).thenReturn(driver);
        doThrow(new ForbiddenException("User is not admin")).when(userValidation).validateUserIs(driver, Role.ADMIN);

        assertThrows(ForbiddenException.class, () -> service.getBusByParam(BusSort.ID, "bus-1", "driver@email.com"));

        verify(userRepository).findByEmailIgnoreCase("driver@email.com");
        verify(userValidation).validateUserExists(Optional.of(driver));
        verify(userValidation).validateUserIs(driver, Role.ADMIN);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldThrowWhenGetBusByParamWithNonExistentBus() {
        var admin = new User();
        admin.setId("2");
        admin.setEmail("test@email.com");
        admin.setRole(Role.ADMIN);

        when(userRepository.findByEmailIgnoreCase("test@email.com")).thenReturn(Optional.of(admin));
        when(userValidation.validateUserExists(Optional.of(admin))).thenReturn(admin);
        doNothing().when(userValidation).validateUserIs(admin, Role.ADMIN);

        when(repository.findByParam("ID", "nonexistent-bus")).thenReturn(Optional.empty());
        doThrow(new ForbiddenException("Bus not found")).when(validation).validateBusExists(Optional.empty());

        assertThrows(ForbiddenException.class, () -> service.getBusByParam(BusSort.ID, "nonexistent-bus", "test@email.com"));

        verify(userRepository).findByEmailIgnoreCase("test@email.com");
        verify(userValidation).validateUserExists(Optional.of(admin));
        verify(userValidation).validateUserIs(admin, Role.ADMIN);
        verify(repository).findByParam("ID", "nonexistent-bus");
        verify(validation).validateBusExists(Optional.empty());
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void shouldGetAllBuses() {
        var admin = new User();
        admin.setId("2");
        admin.setEmail("test@email.com");
        admin.setRole(Role.ADMIN);

        var bus1 = new Bus();
        bus1.setId("bus-1");
        bus1.setName("Bus 1");

        var bus2 = new Bus();
        bus2.setId("bus-2");
        bus2.setName("Bus 2");

        var responseDto1 = new BusResponseDto();
        responseDto1.setId("bus-1");
        responseDto1.setName("Bus 1");

        var responseDto2 = new BusResponseDto();
        responseDto2.setId("bus-2");
        responseDto2.setName("Bus 2");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Bus> busPage = new PageImpl<>(List.of(bus1, bus2));
        Page<BusResponseDto> dtoPage = new PageImpl<>(List.of(responseDto1, responseDto2));

        when(userRepository.findByEmailIgnoreCase("test@email.com")).thenReturn(Optional.of(admin));
        when(userValidation.validateUserExists(Optional.of(admin))).thenReturn(admin);
        doNothing().when(userValidation).validateUserIs(admin, Role.ADMIN);

        when(repository.findAll(pageable)).thenReturn(busPage);
        when(mapper.toDto(bus1)).thenReturn(responseDto1);
        when(mapper.toDto(bus2)).thenReturn(responseDto2);

        var result = service.getAllBuses(pageable, "test@email.com");

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("bus-1", result.getContent().get(0).getId());
        assertEquals("bus-2", result.getContent().get(1).getId());

        verify(userRepository).findByEmailIgnoreCase("test@email.com");
        verify(userValidation).validateUserExists(Optional.of(admin));
        verify(userValidation).validateUserIs(admin, Role.ADMIN);
        verify(repository).findAll(pageable);
        verify(mapper).toDto(bus1);
        verify(mapper).toDto(bus2);
    }

    @Test
    void shouldReturnEmptyPageWhenNoBusesExist() {
        var admin = new User();
        admin.setId("2");
        admin.setEmail("test@email.com");
        admin.setRole(Role.ADMIN);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Bus> emptyBusPage = new PageImpl<>(List.of());
        Page<BusResponseDto> emptyDtoPage = new PageImpl<>(List.of());

        when(userRepository.findByEmailIgnoreCase("test@email.com")).thenReturn(Optional.of(admin));
        when(userValidation.validateUserExists(Optional.of(admin))).thenReturn(admin);
        doNothing().when(userValidation).validateUserIs(admin, Role.ADMIN);

        when(repository.findAll(pageable)).thenReturn(emptyBusPage);

        var result = service.getAllBuses(pageable, "test@email.com");

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(userRepository).findByEmailIgnoreCase("test@email.com");
        verify(userValidation).validateUserExists(Optional.of(admin));
        verify(userValidation).validateUserIs(admin, Role.ADMIN);
        verify(repository).findAll(pageable);
    }

    @Test
    void shouldThrowWhenGetAllBusesWithNonExistentRequester() {
        Pageable pageable = PageRequest.of(0, 10);
        var exception = new NotFoundException("not found");

        when(userRepository.findByEmailIgnoreCase("nonexistent@email.com")).thenReturn(Optional.empty());
        when(userValidation.validateUserExists(Optional.empty())).thenThrow(exception);
        assertThrows(NotFoundException.class, () -> service.getAllBuses(pageable, "nonexistent@email.com"), exception.getMessage());

        verify(userRepository).findByEmailIgnoreCase("nonexistent@email.com");
        verify(userValidation).validateUserExists(Optional.empty());
        verifyNoMoreInteractions(userValidation, repository, mapper);
    }

    @Test
    void shouldThrowWhenGetAllBusesWithNonAdminRequester() {
        var driver = new User();
        driver.setId("1");
        driver.setRole(Role.DRIVER);

        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findByEmailIgnoreCase("driver@email.com")).thenReturn(Optional.of(driver));
        when(userValidation.validateUserExists(Optional.of(driver))).thenReturn(driver);
        doThrow(new ForbiddenException("User is not admin")).when(userValidation).validateUserIs(driver, Role.ADMIN);

        assertThrows(ForbiddenException.class, () -> service.getAllBuses(pageable, "driver@email.com"));

        verify(userRepository).findByEmailIgnoreCase("driver@email.com");
        verify(userValidation).validateUserExists(Optional.of(driver));
        verify(userValidation).validateUserIs(driver, Role.ADMIN);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldThrowWhenUpdateBusWithNonExistentRequester() {
        var request = new BusRequestDto();
        Pageable pageable = PageRequest.of(0, 10);

        var exception = new NotFoundException("not found");

        when(userRepository.findByEmailIgnoreCase("nonexistent@email.com")).thenReturn(Optional.empty());
        when(userValidation.validateUserExists(Optional.empty())).thenThrow(exception);

        assertThrows(NotFoundException.class, () -> service.updateBus(request, "bus-1", "nonexistent@email.com"), exception.getMessage());

        verify(userRepository).findByEmailIgnoreCase("nonexistent@email.com");
        verify(userValidation).validateUserExists(Optional.empty());
        verifyNoMoreInteractions(userValidation, repository, mapper);
    }

    @Test
    void shouldThrowWhenUpdateBusWithNonAdminRequester() {
        var request = new BusRequestDto();
        var driver = new User();
        driver.setId("1");
        driver.setRole(Role.DRIVER);

        when(userRepository.findByEmailIgnoreCase("driver@email.com")).thenReturn(Optional.of(driver));
        when(userValidation.validateUserExists(Optional.of(driver))).thenReturn(driver);
        doThrow(new ForbiddenException("User is not admin")).when(userValidation).validateUserIs(driver, Role.ADMIN);

        assertThrows(ForbiddenException.class, () -> service.updateBus(request, "bus-1", "driver@email.com"));

        verify(userRepository).findByEmailIgnoreCase("driver@email.com");
        verify(userValidation).validateUserExists(Optional.of(driver));
        verify(userValidation).validateUserIs(driver, Role.ADMIN);
        verifyNoMoreInteractions(repository, mapper);
    }

}
