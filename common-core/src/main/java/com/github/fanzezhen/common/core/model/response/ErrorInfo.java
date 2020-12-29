package com.github.fanzezhen.common.core.model.response;

import cn.stylefeng.roses.kernel.model.exception.AbstractBaseExceptionEnum;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author zezhen.fan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorInfo implements Serializable {
    /**
     * 消息code
     */
    private int code;

    private String message;

    private List<Object> arguments;

    public ErrorInfo(String message) {
        this.message = message;
    }

    public ErrorInfo(AbstractBaseExceptionEnum exceptionEnum) {
        this.code = exceptionEnum.getCode();
        this.message = exceptionEnum.getMessage();
    }

    public ErrorInfo(ServiceException serviceException) {
        this.code = serviceException.getCode();
        this.message = serviceException.getMessage();
    }
}