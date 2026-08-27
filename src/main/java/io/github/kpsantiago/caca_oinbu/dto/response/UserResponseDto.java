package io.github.kpsantiago.caca_oinbu.dto.response;

import io.github.kpsantiago.caca_oinbu.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserResponseDto {
    private String id;
    private String name;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
}
