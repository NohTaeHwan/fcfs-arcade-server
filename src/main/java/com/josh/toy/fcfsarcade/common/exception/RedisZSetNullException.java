package com.josh.toy.fcfsarcade.common.exception;

public class RedisZSetNullException extends BusinessException {
    public RedisZSetNullException() {
        super(ErrorCode.NOT_FOUND.value());
    }
    public RedisZSetNullException(int value) {
        super(value);
    }
}
