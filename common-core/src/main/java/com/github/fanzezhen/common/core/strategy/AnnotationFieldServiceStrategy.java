package com.github.fanzezhen.common.core.strategy;

import cn.hutool.core.util.StrUtil;
import com.github.fanzezhen.common.core.service.AbstractAnnotationFieldService;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zezhen.fan
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class AnnotationFieldServiceStrategy {
    private AnnotationFieldServiceStrategy() {
    }

    static Map<String, AbstractAnnotationFieldService> SERVICE_MAP;
    static Set<String> annotationNameSet = null;

    public static void setAnnotationFieldValue(Object bean, Object fieldValue, Annotation annotation, String fieldName, Map<String, Object[]> setterMap) {
        SERVICE_MAP.get(StringUtils.uncapitalize(annotation.annotationType().getSimpleName()) + "Service").setAnnotationField(bean, fieldValue, annotation, fieldName, setterMap);
    }

    public static Set<String> getAnnotationNameSet() {
        if (annotationNameSet == null) {
            annotationNameSet = SERVICE_MAP.keySet().stream().map(name -> StringUtils.capitalize(StrUtil.removeSuffix(name, "Service"))).collect(Collectors.toSet());
        }
        return annotationNameSet;
    }
}
