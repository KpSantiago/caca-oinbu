package io.github.kpsantiago.caca_oinbu.validation;

import io.github.kpsantiago.caca_oinbu.exception.ConflictException;
import io.github.kpsantiago.caca_oinbu.exception.NotFoundException;
import io.github.kpsantiago.caca_oinbu.model.Bus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BusValidation {

    public Bus validateBusExists(Optional<Bus> bus) {
        return bus.orElseThrow(() -> new NotFoundException("Bus not found"));
    }

    public void validateBusDoesNotExist(Optional<Bus> bus) {
        bus.ifPresent(b -> {
            throw new ConflictException("Bus already exists");
        });
    }
}
