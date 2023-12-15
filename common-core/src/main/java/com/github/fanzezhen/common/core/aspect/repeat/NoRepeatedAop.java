package com.github.fanzezhen.common.core.aspect.repeat;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.fanzezhen.common.core.context.SysContextHolder;
import com.github.fanzezhen.common.core.service.CacheService;
import com.github.fanzezhen.common.core.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

import static java.util.stream.Collectors.toList;

/**
 * @author zezhen.fan
 */
@Slf4j
@Aspect
@Component("CommonNoRepeatedAop")
public class NoRepeatedAop {
    @Value("${spring.application.name:}")
    private String springApplicationName;
    /**
     * 环境隔离变量
     */
    @Value("${spring.profiles.active:}")
    private String env;
    private final CacheService cacheService;

    @Autowired(required = false)
    public NoRepeatedAop(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * 要处理的方法，包名+类名+方法名
     */
    @Pointcut("@annotation(com.github.fanzezhen.common.core.aspect.repeat.NoRepeat)")
    public void cut() {
    }

    /**
     * 在调用上面 @Pointcut标注的方法前执行以下方法
     *
     * @param joinPoint JoinPoint
     */
    @Before("cut()")
    public void doBefore(JoinPoint joinPoint) {
        String key;
        String value;
        NoRepeat noRepeat;
        try {
            noRepeat = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(NoRepeat.class);
            if (noRepeat == null) {
                return;
            }
            key = getKey(joinPoint, noRepeat);
            if (cacheService == null) {
                log.warn("noRepeated check skip cacheService is null");
                return;
            }
            value = cacheService.get(key);
        } catch (Throwable throwable) {
            log.error("noRepeated check failed exception", throwable);
            return;
        }
        if (!CharSequenceUtil.isEmpty(value)) {
            ExceptionUtil.throwException("请勿重复提交");
        }
        try {
            cacheService.set(key, String.valueOf(System.currentTimeMillis()), noRepeat.timeout(), noRepeat.timeUnit());
        } catch (Throwable throwable) {
            log.error("noRepeated put failed exception", throwable);
        }
    }

    private String getKey(JoinPoint joinPoint, NoRepeat noRepeat) {
        Object[] args = joinPoint.getArgs();
        JSONObject param = new JSONObject();
        String[] headerArgs = noRepeat.headerArgs();
        if (ArrayUtil.isNotEmpty(headerArgs)) {
            for (String headerKey : headerArgs) {
                if (CharSequenceUtil.isBlank(headerKey)) {
                    continue;
                }
                param.put(headerKey, SysContextHolder.get(headerKey));
            }
        }
        String[] paramArgs = noRepeat.paramArgs();
        if (ArrayUtil.isNotEmpty(paramArgs) && ArrayUtil.isNotEmpty(args)) {
            for (String validArg : paramArgs) {
                if (CharSequenceUtil.isBlank(validArg)) {
                    continue;
                }
                String[] fieldParts = validArg.split("\\.");
                if (ArrayUtil.isEmpty(fieldParts)) {
                    continue;
                }
                if (NumberUtil.isInteger(fieldParts[0])) {
                    int i = Integer.parseInt(fieldParts[0]);
                    if (i < args.length) {
                        Object o = args[i];
                        if (fieldParts.length > 1) {
                            for (int j = 1; j < fieldParts.length; j++) {
                                o = ReflectUtil.getFieldValue(o, fieldParts[j]);
                            }
                        }
                        param.put(validArg, o);
                    }
                }
            }
        }
        String headerJsonStr = SysContextHolder.getHeaderJsonStr(noRepeat.headerArgs());
        String paramKey = noRepeat.key();
        if (CharSequenceUtil.isEmpty(paramKey)) {
            paramKey = JSON.toJSONString(Arrays.stream(args).filter(arg -> !(arg instanceof HttpServletRequest)).collect(toList()));
        }
        String key = env + StrUtil.SLASH + springApplicationName + StrUtil.SLASH + "NoRepeat" + StrUtil.SLASH + joinPoint.getTarget().getClass().getName() + StrUtil.DOT + joinPoint.getSignature().getName() + StrUtil.SLASH + paramKey + StrUtil.SLASH + headerJsonStr + StrUtil.SLASH + param.toJSONString();
        log.info("key={}", key);
        return key;
    }

}
