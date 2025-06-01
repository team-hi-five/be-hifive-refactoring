package com.h5.global.response;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultResponse<T> {

    private static final String DEFAULT_SUCCESS_MESSAGE = "Success";

    private int status;
    private String code;
    private String message;
    private T results;

    public static <T> ResultResponse<T> success() {
        return ResultResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .code(String.valueOf(HttpStatus.OK.value()))
                .message(DEFAULT_SUCCESS_MESSAGE)
                .build();
    }

    public static <T> ResultResponse<T> success(T results) {
        return ResultResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .code(String.valueOf(HttpStatus.OK.value()))
                .message(DEFAULT_SUCCESS_MESSAGE)
                .results(results)
                .build();
    }

    public static <T> ResultResponse<T> success(HttpStatus status, T results) {
        return ResultResponse.<T>builder()
                .status(status.value())
                .code(String.valueOf(status.value()))
                .message(DEFAULT_SUCCESS_MESSAGE)
                .results(results)
                .build();
    }
}