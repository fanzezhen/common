package com.github.fanzezhen.common.core.util;

import cn.stylefeng.roses.kernel.model.exception.AbstractBaseExceptionEnum;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;

/**
 * @author zezhen.fan
 */
public class ExceptionUtil {
    public static void throwException(int code, String errorMessage) {
        throw new ServiceException(code, errorMessage);
    }

    public static void throwException(String errorMessage) {
        throw new ServiceException(CoreExceptionEnum.SERVICE_ERROR.getCode(), errorMessage);
    }

    public static void throwException(AbstractBaseExceptionEnum exceptionEnum) {
        throw new ServiceException(exceptionEnum);
    }
}
