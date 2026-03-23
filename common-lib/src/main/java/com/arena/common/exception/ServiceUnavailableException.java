package com.arena.common.exception;

import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends BaseException {

    public ServiceUnavailableException(String service) {
        super(
                String.format("%s is currently unavailable. Please try again later.", service),
                HttpStatus.SERVICE_UNAVAILABLE,
                "SERVICE_UNAVAILABLE"
        );
    }
}