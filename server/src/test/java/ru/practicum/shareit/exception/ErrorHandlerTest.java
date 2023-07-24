package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import java.util.Map;

class ErrorHandlerTest {
    private ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleException() {
        Map<String, String> handleNotFoundException = errorHandler
                .handleNotFoundException(new NotFoundException("NotFoundException"));

        Map<String, String> handleConflictException = errorHandler
                .handleConflictException(new ConflictException("ConflictException"));

        Map<String, String> handleBadRequestException = errorHandler
                .handleBadRequestException(new BadRequestException("BadRequestException"));
    }
}