package com.h5.global.exception;

import com.github.hyeonjaez.springcommon.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum DomainErrorCode implements ErrorCode {

    ACCESS_TOKEN_NOTFOUND(HttpStatus.NOT_FOUND, "AUTH-001", "Access Token Not Found"),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "AUTH-002", "JWT Expired"),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "AUTH-003", "JWT Invalid"),
    AUTHENTICATION_FAILED(HttpStatus.FORBIDDEN, "AUTH-004", "Authentication Failed"),
    INVALID_ROLE(HttpStatus.FORBIDDEN, "AUTH-005", "Invalid Role"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "User Not Found"),
    OLD_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "USER-002", "Old Password Mismatch"),

    GAME_ASSET_NOT_FOUND(HttpStatus.NOT_FOUND, "GAME-ASSET-001", "Game Asset Not Found"),

    CHATBOT_NOT_FOUND(HttpStatus.NOT_FOUND, "CHATBOT-001", "ChatBot Not Found"),
    CHATBOT_ALREADY_SAVED(HttpStatus.CONFLICT, "CHATBOT-002", "ChatBot Already Saved"),

    MAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL-001", "Mail Send Failed"),

    DELETE_REQUEST_DUPLICATED(HttpStatus.CONFLICT, "DELETE-001", "Delete Request Duplicated"),

    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "BOARD-001", "Board Not Found"),
    BOARD_ACCESS_DENY(HttpStatus.FORBIDDEN, "BOARD-002", "Board Access Denied"),

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT-001", "Comment Not Found"),
    COMMENT_ACCESS_DENY(HttpStatus.UNAUTHORIZED, "COMMENT-002", "Comment Access Denied"),

    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE-001", "File Not Found"),
    FILE_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-002", "File Load Failed"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-003", "File Upload Failed"),
    INVALID_FILE_INPUT(HttpStatus.BAD_REQUEST, "FILE-004", "Invalid File Input"),

    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "GAME-001", "Game Not Found"),
    GAME_ACCESS_DENY(HttpStatus.FORBIDDEN, "GAME-002", "Game Access Denied"),

    SCHEDULE_CONFLICT(HttpStatus.CONFLICT, "SCHEDULE-001", "Schedule Conflict"),
    INVALID_SCHEDULE_TYPE(HttpStatus.BAD_REQUEST, "SCHEDULE-002", "Invalid Schedule Type"),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE-003", "Schedule Not Found"),
    SCHEDULE_OVER_TIME(HttpStatus.BAD_REQUEST, "SCHEDULE-004", "Schedule Over Time"),
    SCHEDULE_NOT_STARTED(HttpStatus.PRECONDITION_FAILED, "SCHEDULE-005", "Schedule Not Started"),

    STATISTIC_NOT_FOUND(HttpStatus.NOT_FOUND, "STATISTIC-001", "Statistic Not Found"),
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