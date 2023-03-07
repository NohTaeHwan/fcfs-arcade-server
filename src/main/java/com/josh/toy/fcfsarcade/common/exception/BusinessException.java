package com.josh.toy.fcfsarcade.common.exception;

import com.josh.toy.fcfsarcade.common.model.ApiResponse;

public class BusinessException extends RuntimeException {
    private final ApiResponse apiResponse;

    public BusinessException(int code) {
        this.apiResponse = new ApiResponse(code);
    }

    public ApiResponse getBusinessException(){
        return this.apiResponse;
    }

}
