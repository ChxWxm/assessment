package com.kbtg.bootcamp.posttest.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class InvalidArgumentResponse {
    private final Map<String, String> messages;
    private final HttpStatus status;

    public InvalidArgumentResponse(Map<String, String> messages, HttpStatus status) {
        this.messages = messages;
        this.status = status;
    }
}
