package io.github.kpsantiago.caca_oinbu.config;

import io.github.kpsantiago.caca_oinbu.exception.BadRequestException;
import io.github.kpsantiago.caca_oinbu.exception.BusinessException;
import io.github.kpsantiago.caca_oinbu.exception.obj.ErrorObject;
import io.github.kpsantiago.caca_oinbu.exception.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.lang.reflect.UndeclaredThrowableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorObject> handleAuthenticationException(AuthenticationException ex) {
        if (ex.getClass().isInstance(BadCredentialsException.class))
            return handleBusinessException(new BadRequestException("Usuário ou senha inválidos"));

        return handleBusinessException(new ForbiddenException("Autenticação inválida"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorObject> handleRuntimeException(RuntimeException ex) {
        if (ex.getClass().isAssignableFrom(UndeclaredThrowableException.class)) {
            var exception = (UndeclaredThrowableException) ex;
            return handleBusinessException((BusinessException) exception.getCause());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorObject(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage()
        ));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorObject> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(ex.getStatus().value()).body(new ErrorObject(
                ex.getStatus().getReasonPhrase(),
                ex.getStatus().value(),
                ex.getMessage()
        ));
    }
}
