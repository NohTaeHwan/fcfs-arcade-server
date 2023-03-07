package com.josh.toy.fcfsarcade.common.exception;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException() {
        super(ErrorCode.NOT_FOUND.value());
    }
    public EntityNotFoundException(int value) {
        super(value);
    }

}
