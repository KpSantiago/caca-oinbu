package io.github.kpsantiago.caca_oinbu.repository;

import io.github.kpsantiago.caca_oinbu.enums.BusSort;
import io.github.kpsantiago.caca_oinbu.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BusRepository extends JpaRepository<Bus, String> {
    @Query("""
        SELECT b FROM Bus b INNER JOIN b.driver WHERE :param = :value
    """)
    Optional<Bus> findByParam(@Param(":param") BusSort param, @Param(":value") String value);
    Optional<Bus> findByPlateIgnoreCase(String plate);
}
