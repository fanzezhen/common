package com.github.fanzezhen.common.core.util;

import cn.hutool.core.collection.CollectionUtil;

import javax.validation.*;
import java.util.Set;

/**
 * @author zezhen.fan
 */
public class ValidationUtil {

    public static <T> Set<ConstraintViolation<T>> validate(T bean) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(bean);
        return violations;
    }

    public static <T> void throwInValidate(T bean) {
        Set<ConstraintViolation<T>> violations = validate(bean);
        if (CollectionUtil.isEmpty(violations)) {
            return;
        }
        throw new ConstraintViolationException(violations.iterator().next().getMessage(), violations);
    }
}
