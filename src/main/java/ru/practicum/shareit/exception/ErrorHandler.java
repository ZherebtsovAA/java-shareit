package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflictException(final ConflictException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestException(final BadRequestException exception) {
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(final ConstraintViolationException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getConstraintViolations().forEach((violation) -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException exception) {
        if (exception.getName().equals("state") && Objects.equals(exception.getValue(), "UNSUPPORTED_STATUS")) {
            return Map.of("error", "Unknown state: UNSUPPORTED_STATUS");
        }

        return Map.of("error", Objects.requireNonNull(exception.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleThrowable(final Throwable exception) {
        return Map.of("error", "Произошла непредвиденная ошибка.");
    }

}