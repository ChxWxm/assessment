package com.kbtg.bootcamp.posttest.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiExceptionResponse {
    private final String message;
    private final HttpStatus status;

    public ApiExceptionResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
