package com.josh.toy.fcfsarcade.common.exception;

public class ArcadeException extends BusinessException{

    public ArcadeException(){super(ErrorCode.INTERNAL_SERVER_ERROR.value());}
}
