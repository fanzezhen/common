package com.github.fanzezhen.common.core.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

import javax.validation.*;
import java.util.Set;

/**
 * @author zezhen.fan
 */
public class ValidationUtil {

    public static <T> void validate(T bean) {
        validate(bean, StrUtil.EMPTY, StrUtil.EMPTY);
    }

    public static <T> void validate(T bean, String startMsg, String endMsg) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(bean);
        if (CollectionUtil.isEmpty(violations)) {
            return;
        }
        throw new ConstraintViolationException(startMsg + violations.iterator().next().getMessage() + endMsg, violations);
    }

    public static <T> Set<ConstraintViolation<T>> loadViolationSet(T bean) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(bean);
    }

    public static <T> void throwInValidate(T bean) {
        Set<ConstraintViolation<T>> violations = loadViolationSet(bean);
        if (CollectionUtil.isEmpty(violations)) {
            return;
        }
        throw new ConstraintViolationException(violations.iterator().next().getMessage(), violations);
    }
}
