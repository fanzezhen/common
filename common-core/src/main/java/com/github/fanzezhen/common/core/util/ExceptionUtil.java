package com.github.fanzezhen.common.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.stylefeng.roses.kernel.model.exception.AbstractBaseExceptionEnum;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * @author zezhen.fan
 */
public class ExceptionUtil {
    private ExceptionUtil() {
    }

    public static ServiceException wrapException(String errorMessage) {
        return new ServiceException(CoreExceptionEnum.SERVICE_ERROR.getCode(), errorMessage);
    }

    public static ServiceException wrapException(int code, String errorMessage) {
        return new ServiceException(code, errorMessage);
    }

    public static void throwException(int code, String errorMessage) {
        throw new ServiceException(code, errorMessage);
    }

    public static void throwException(String errorMessage) {
        throw new ServiceException(CoreExceptionEnum.SERVICE_ERROR.getCode(), errorMessage);
    }

    public static void throwException(AbstractBaseExceptionEnum exceptionEnum) {
        throw new ServiceException(exceptionEnum);
    }

    public static void throwIfBlank(Object param, String paramName) {
        throwIfBlank(param, paramName, "参数不能为空");
    }

    public static void throwIfBlank(Object param, String paramName, String errMsg) {
        if (isBlank(param)) {
            ExceptionUtil.throwException(paramName + errMsg);
        }
    }

    public static boolean isEmpty(Object o) {
        return isEmpty(o, false);
    }

    public static boolean isEmpty(Object o, boolean isStrip) {
        if (o == null) {
            return true;
        }
        if (isStrip) {
            if (CharSequenceUtil.isBlank(String.valueOf(o))) {
                return true;
            }
        } else {
            if (CharSequenceUtil.isEmpty(String.valueOf(o))) {
                return true;
            }
        }
        if (o instanceof Map && MapUtil.isEmpty((Map<?, ?>) o)) {
            return true;
        }
        if (o instanceof Collection && CollUtil.isEmpty((Collection<?>) o)) {
            return true;
        }
        if (o instanceof byte[] bytes && bytes.length == 0) {
            return true;
        }
        return o instanceof String[] strings && strings.length == 0;
    }

    public static boolean isNotBlank(Object o) {
        return !isBlank(o);
    }

    public static void throwIfBlank(Object param) {
        throwIfBlank(param, StrUtil.EMPTY);
    }

    public static void throwIfEmpty(Object param, String paramName, String errMsg) {
        if (param == null || CharSequenceUtil.isEmpty(String.valueOf(param))) {
            ExceptionUtil.throwException(paramName + errMsg);
        }
        if (param instanceof Map && MapUtil.isEmpty((Map<?, ?>) param)) {
            ExceptionUtil.throwException(paramName + errMsg);
        }
        if (param instanceof Collection && CollUtil.isEmpty((Collection<?>) param)) {
            ExceptionUtil.throwException(paramName + errMsg);
        }
        if (param instanceof byte[] bytes&& bytes.length == 0) {
            ExceptionUtil.throwException(paramName + errMsg);
        }
        if (param instanceof String[]strings &&strings.length == 0) {
            ExceptionUtil.throwException(paramName + errMsg);
        }
    }

    public static boolean isBlank(Object o) {
        return isEmpty(o, true);
    }

    public static void throwIfEmpty(Object param, String paramName) {
        throwIfEmpty(param, paramName, " 不能为空");
    }

    public static void throwIfEmpty(Object param) {
        throwIfEmpty(param, StrUtil.EMPTY);
    }

    public static void throwIf(boolean bool, String errMsg) {
        if (bool) {
            throwException(errMsg);
        }
    }

    public static void throwIf(boolean bool, int errCode, String errMsg) {
        if (bool) {
            throwException(errCode, errMsg);
        }
    }

    public static void throwOfSize(Object o, Integer min, Integer max, String name) {
        throwOfSize(o, min, max, name + "不能小于" + min, name + "不能大于" + max);
    }

    public static void throwOfSize(Object o, Integer min, Integer max, String minErrMsg, String maxErrMsg) {
        if (o == null) {
            ExceptionUtil.throwIf(min != null, minErrMsg);
        } else if (o instanceof CharSequence charSequence) {
            throwOfSizeCharSequence(charSequence, min, max, minErrMsg, maxErrMsg);
        } else if (o instanceof Map<?, ?> map) {
            throwOfSizeMap(map, min, max, minErrMsg, maxErrMsg);
        } else if (o instanceof Collection<?> collection) {
            throwOfSizeCollection(collection, min, max, minErrMsg, maxErrMsg);
        } else if (o.getClass().isArray()) {
            if (min != null) {
                ExceptionUtil.throwIf(Array.getLength(o) < min, minErrMsg);
            }
            if (max != null) {
                ExceptionUtil.throwIf(Array.getLength(o) > max, maxErrMsg);
            }
        }
    }

    public static void throwOfSizeCharSequence(CharSequence o, Integer min, Integer max, String minErrMsg, String maxErrMsg) {
        if (min != null) {
            ExceptionUtil.throwIf(o.length() < min, minErrMsg);
        }
        if (max != null) {
            ExceptionUtil.throwIf(o.length() > max, maxErrMsg);
        }
    }

    public static void throwOfSizeCollection(Collection<?> o, Integer min, Integer max, String minErrMsg, String maxErrMsg) {
        if (min != null) {
            ExceptionUtil.throwIf(o.size() < min, minErrMsg);
        }
        if (max != null) {
            ExceptionUtil.throwIf(o.size() > max, maxErrMsg);
        }
    }

    public static void throwOfSizeMap(Map<?, ?> o, Integer min, Integer max, String minErrMsg, String maxErrMsg) {
        if (min != null) {
            ExceptionUtil.throwIf(o.size() < min, minErrMsg);
        }
        if (max != null) {
            ExceptionUtil.throwIf(o.size() > max, maxErrMsg);
        }
    }
}
