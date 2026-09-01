package io.github.kpsantiago.caca_oinbu.enums;

import lombok.Getter;

@Getter
public enum BusSort {
    ID("b.id"),
    NAME("b.name"),
    PLATE("b.plate"),
    DRIVER("b.driver.name");

    private final String value;
    BusSort(String value) {
        this.value = value;
    }
}
