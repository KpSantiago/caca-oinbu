package io.github.kpsantiago.caca_oinbu.repository;

import io.github.kpsantiago.caca_oinbu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmailIgnoreCase(String email);
}
