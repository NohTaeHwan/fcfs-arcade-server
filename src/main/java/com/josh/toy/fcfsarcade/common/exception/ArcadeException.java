package com.josh.toy.fcfsarcade.common.exception;

public class ArcadeException extends BusinessException{

    public ArcadeException(){super(ErrorCode.ARCADE_ERROR.value());}

    public ArcadeException(int value){
        super(value);
    }
}
