package com.h5.global.exception;

import com.github.hyeonjaez.springcommon.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum DomainErrorCode implements ErrorCode {

    ACCESS_TOKEN_NOTFOUND(HttpStatus.NOT_FOUND, "AUTH-001", "Access Token Not Found"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "User Not Found"),
    OLD_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "OLD-PASSWORD-MISMATCH", "Old Password Mismatch"),

    GAME_ASSET_NOT_FOUND(HttpStatus.NOT_FOUND, "GAME-ASSET-001", "Game Asset Not Found"),

    CHATBOT_NOT_FOUND(HttpStatus.NOT_FOUND, "CHATBOT-001", "ChatBot Not Found"),
    CHATBOT_ALREADY_SAVED(HttpStatus.CONFLICT, "CHATBOT-001", "ChatBot Already Saved"),

    MAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL-001", "Mail Send Failed"),

    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH-001", "Authentication Failed"),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "INVALID-ROLE", "Invalid Role"),

    DELETE_REQUEST_DUPLICATED(HttpStatus.CONFLICT, "DELETE-001", "Delete Request Duplicated"),

    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD-001", "Board Not Found"),
    BOARD_ACCESS_DENY(HttpStatus.UNAUTHORIZED, "BOARD-001", "Board Access Denied"),

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT-001", "Comment Not Found"),
    COMMENT_ACCESS_DENY(HttpStatus.UNAUTHORIZED, "COMMENT-001", "Comment Access Denied"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    DomainErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}