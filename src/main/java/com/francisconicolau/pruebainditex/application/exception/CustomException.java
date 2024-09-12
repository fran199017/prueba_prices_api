package com.francisconicolau.pruebainditex.application.exception;

import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class CustomException extends RuntimeException implements Supplier<CustomException> {

    private final Integer statusExceptionCode;

    public CustomException(Integer statusExceptionCode, String message) {
        super(message);
        this.statusExceptionCode = statusExceptionCode;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    @Override
    public CustomException get() {
        return this;
    }
}
