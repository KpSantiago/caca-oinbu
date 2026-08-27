package io.github.kpsantiago.caca_oinbu.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    DRIVER,
    USER;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
