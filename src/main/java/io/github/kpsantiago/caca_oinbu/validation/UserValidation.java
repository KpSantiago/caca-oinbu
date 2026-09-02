package io.github.kpsantiago.caca_oinbu.validation;

import io.github.kpsantiago.caca_oinbu.enums.Role;
import io.github.kpsantiago.caca_oinbu.exception.ConflictException;
import io.github.kpsantiago.caca_oinbu.exception.ForbiddenException;
import io.github.kpsantiago.caca_oinbu.exception.NotFoundException;
import io.github.kpsantiago.caca_oinbu.model.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserValidation {

    public User validateUserExists(Optional<User> user, boolean shouldExists) {
        if (!shouldExists && user.isPresent()) {
            throw new ConflictException("User already exists");
        }

        return user.orElseThrow(() -> new NotFoundException("User not found"));
    }

    public void validateUserIs(User user, Role role) {
        if (!user.getRole().equals(role)) {
            throw new ForbiddenException("User must be " + role);
        }
    }
}
