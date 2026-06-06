package com.shusixue.exception;

import com.shusixue.common.ResultCode;
import lombok.Getter;

/**
 * 自定义业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.resultCode = resultCode;
    }

    public BusinessException(String msg) {
        super(msg);
        this.resultCode = ResultCode.FAIL;
    }
}