package com.github.fanzezhen.common.core.aop;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.fanzezhen.common.core.annotion.NoRepeat;
import com.github.fanzezhen.common.core.context.SysContext;
import com.github.fanzezhen.common.core.enums.NoRepeatTypeEnum;
import com.github.fanzezhen.common.core.service.CacheService;
import com.github.fanzezhen.common.core.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static java.util.stream.Collectors.toList;

/**
 * @author zezhen.fan
 */
@Slf4j
@Aspect
@Component("FzzNoRepeatedAop")
public class NoRepeatedAop {
    @Resource(name = "${com.github.fanzezhen.common.core.common.no-repeated.cache-service-bean:FzzCacheServiceImpl}")
    private CacheService cacheService;
    @Value("${spring.application.name:}")
    private String springApplicationName;
    /**
     * 环境分离变量
     */
    @Value("${spring.profiles.active:}")
    private String env;

    /**
     * 要处理的方法，包名+类名+方法名
     */
    @Pointcut("@annotation(com.github.fanzezhen.common.core.annotion.NoRepeat)")
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
            if (noRepeat == null || !NoRepeatTypeEnum.TIME.equals(noRepeat.type())) {
                return;
            }
            key = getKey(joinPoint, noRepeat);
            value = cacheService.get(key);
        } catch (Throwable throwable) {
            log.error("noRepeated check failed exception", throwable);
            return;
        }
        if (!StrUtil.isEmpty(value)) {
            ExceptionUtil.throwException("请勿重复提交");
        }
        try {
            cacheService.put(key, String.valueOf(System.currentTimeMillis()), noRepeat.timeoutMillis());
        } catch (Throwable throwable) {
            log.error("noRepeated put failed exception", throwable);
        }
    }

    private String getKey(JoinPoint joinPoint, NoRepeat noRepeat) {
        Object[] args = joinPoint.getArgs();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        JSONObject param = new JSONObject();
        String[] headerArgs = noRepeat.headerArgs();
        if (ArrayUtil.isNotEmpty(headerArgs)) {
            for (String headerKey : headerArgs) {
                if (StrUtil.isBlank(headerKey)) {
                    continue;
                }
                param.put(headerKey, SysContext.get(headerKey));
            }
        }
        String[] paramArgs = noRepeat.paramArgs();
        if (ArrayUtil.isNotEmpty(paramArgs) && ArrayUtil.isNotEmpty(args)) {
            for (String validArg : paramArgs) {
                if (StrUtil.isBlank(validArg)) {
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
        String paramStr;
        if (param.isEmpty()) {
            paramStr = JSON.toJSONString(Arrays.stream(args).filter(arg -> !(arg instanceof HttpServletRequest)).collect(toList()));
        } else {
            paramStr = param.toJSONString();
        }
        String md5 = SecureUtil.md5(paramStr);
        log.info(env + springApplicationName + "-noRepeat-" + className + methodName + noRepeat.type() + noRepeat.timeoutMillis() + paramStr + md5);
        return env + springApplicationName + "-noRepeat-" + className + methodName + md5;
    }

    /**
     * @param joinPoint 切点
     * @return ActionResult
     */
    @Around("cut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        String key = null;
        String value = null;
        NoRepeat noRepeat = null;
        boolean isTransaction = false;
        try {
            noRepeat = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(NoRepeat.class);
            isTransaction = noRepeat != null && NoRepeatTypeEnum.TRANSACTION.equals(noRepeat.type());
            if (isTransaction) {
                key = getKey(joinPoint, noRepeat);
                value = cacheService.get(key);
            }
        } catch (Throwable throwable) {
            log.error("noRepeated check failed exception", throwable);
        }
        if (!StrUtil.isEmpty(value)) {
            ExceptionUtil.throwException("系统繁忙，请勿重复提交");
        }
        try {
            if (isTransaction) {
                cacheService.put(key, String.valueOf(System.currentTimeMillis()), noRepeat.timeoutMillis());
            }
        } catch (Throwable throwable) {
            log.error("noRepeated put failed exception", throwable);
        }
        try {
            result = joinPoint.proceed();
        } finally {
            try {
                if (key != null) {
                    cacheService.remove(key);
                }
            } catch (Throwable throwable) {
                log.warn("noRepeated TransactionLock freed failed exception", throwable);
            }
        }
        return result;
    }
}