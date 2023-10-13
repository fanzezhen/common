package com.github.fanzezhen.common.core.service;


import com.github.fanzezhen.common.core.model.ClassInfoBean;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author zezhen.fan
 */
public abstract class AbstractAnnotationFieldService<T extends Annotation> {
    public abstract void setAnnotationField(Object bean, Object fieldValue, T annotation, String fieldName);

    public abstract void setAnnotationField(Object bean, Object fieldValue, T annotation, String fieldName, Map<Object, Object[]> setterMap);

    public abstract void setAnnotationField(Object bean, Object dictFieldValue, T annotation, String fieldName, ClassInfoBean classInfo);
}
