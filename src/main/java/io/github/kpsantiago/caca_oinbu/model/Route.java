package io.github.kpsantiago.caca_oinbu.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Route {
    @Id
    @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double distance;

    @Column(name = "start_point", nullable = false)
    private String startPoint;

    @Column(name = "end_point", nullable = false)
    private String endPoint;

    @OneToMany(mappedBy = "route", fetch = FetchType.LAZY)
    private List<RouteSchedule> routeSchedules;
}
