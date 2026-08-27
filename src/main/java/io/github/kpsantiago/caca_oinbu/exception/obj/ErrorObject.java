package io.github.kpsantiago.caca_oinbu.exception.obj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorObject {
    private String error;
    private int status;
    private String message;
}
