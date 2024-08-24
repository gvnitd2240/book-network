package com.vivek.garg.book_network.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;


public enum BusinessErrorCodes {
    NO_CODE(0,NOT_IMPLEMENTED, "no code"),
    ACCOUNT_LOCKED(302, FORBIDDEN, "User account is locked."),
    INCORRECT_PASSWORD(300, HttpStatus.BAD_REQUEST, "Password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(300, HttpStatus.BAD_REQUEST, "NEW_PASSWORD_DOES_NOT_MATCH"),
    ACCOUNT_DISABLED(302, FORBIDDEN, "User account is DISABLED."),
    BAD_CREDENTIALS(302, FORBIDDEN, "BAD_CREDENTIALS")
    ;
    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
