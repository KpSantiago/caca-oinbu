package io.github.kpsantiago.caca_oinbu.exception;

public class BadJwtException extends ForbiddenException {
    public BadJwtException(String message) {
        super(message);
    }
}
