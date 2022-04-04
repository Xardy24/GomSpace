package crr.exchange.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import crr.exchange.util.exception.EntityNotFoundByIdException;
import crr.exchange.util.exception.NotEnoughFundsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ResponseBody
    @ExceptionHandler(EntityNotFoundByIdException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Error entityNotFoundHandler(EntityNotFoundByIdException ex) {
        return new Error("EntityNotFoundByIdException", ex.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(NotEnoughFundsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    Error notEnoughFundsHandler(NotEnoughFundsException ex) {
        return new Error("NotEnoughFundsException", ex.getMessage());
    }

    @Getter
    @NoArgsConstructor
    static class Error {

        private String name, message;

        Error(String name, String message) {
            this.name = name;
            this.message = message;
        }
    }

}
