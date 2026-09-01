package io.github.kpsantiago.caca_oinbu.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Bus {
    @Id
    @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String plate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @OneToMany(mappedBy = "bus")
    private List<RouteSchedule> routeSchedules = new ArrayList<>();

    @PrePersist
    void PrePersist() {
        plate = plate.toUpperCase();
    }

    @PreUpdate
    void PreUpdate() {
        plate = plate.toUpperCase();
    }
}
