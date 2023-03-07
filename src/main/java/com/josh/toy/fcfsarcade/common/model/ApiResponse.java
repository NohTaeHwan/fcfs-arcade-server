package com.josh.toy.fcfsarcade.common.model;

import com.josh.toy.fcfsarcade.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * class : 공통 응답 모델
 */
@Data
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private int statusCode;
    private String message;
    private T data;
    private T errors;

    public ApiResponse(final int statusCode){
        this.statusCode = statusCode;
        this.message = ErrorCode.valueOf(statusCode).getReasonPhrase();
        this.data = null;
        this.errors = null;
    }

    public static<T> ApiResponse<T> fail(final int statusCode){
        return fail(statusCode, null);
    }
    public static<T> ApiResponse<T> fail(final int statusCode, final T data){
        return fail(statusCode, data, null);
    }


    public static<T> ApiResponse<T> fail(final int statusCode, final T t, final T errors) {
        return ApiResponse.<T>builder()
                .errors(errors)
                .data(t)
                .statusCode(statusCode)
                .message(ErrorCode.valueOf(statusCode).getReasonPhrase())
                .build();
    }

    public static<T> ApiResponse<T> succ(){
        return succ(null);
    }

    public static<T> ApiResponse<T> succ(final T t){
        return ApiResponse.<T>builder()
                .data(t)
                .statusCode(200)
                .message(ErrorCode.valueOf(200).getReasonPhrase())
                .build();
    }

}
