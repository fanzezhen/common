package com.github.fanzezhen.common.core.aspect.concurrent;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.fanzezhen.common.core.context.SysContextHolder;
import com.github.fanzezhen.common.core.service.LockService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static java.util.stream.Collectors.toList;

/**
 * @author zezhen.fan
 */
@Slf4j
@Aspect
@Component("CommonNoConcurrentAop")
public class NoConcurrentAop {
    @Value("${spring.application.name:}")
    private String springApplicationName;
    /**
     * 环境隔离变量
     */
    @Value("${spring.profiles.active:}")
    private String env;
    private final LockService lockService;

    @Autowired(required = false)
    public NoConcurrentAop(LockService lockService) {
        this.lockService = lockService;
    }

    @Pointcut("@annotation(com.github.fanzezhen.common.core.aspect.concurrent.NoConcurrent)")
    public void cut() {
    }

    @Around("cut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (lockService == null) {
            return joinPoint.proceed();
        }
        String key = getKey(joinPoint, ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(NoConcurrent.class));
        return lockService.lockKey(key, () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String getKey(JoinPoint joinPoint, NoConcurrent noConcurrent) {
        Object[] args = joinPoint.getArgs();
        String paramKey = noConcurrent.key();
        if (StrUtil.isEmpty(paramKey)) {
            paramKey = JSON.toJSONString(Arrays.stream(args).filter(arg -> !(arg instanceof HttpServletRequest)).collect(toList()));
        }
        String key = env + StrUtil.SLASH +
                springApplicationName + StrUtil.SLASH +
                "NoConcurrent" + StrUtil.SLASH +
                joinPoint.getTarget().getClass().getName() + StrUtil.DOT + joinPoint.getSignature().getName() + StrUtil.SLASH +
                paramKey + StrUtil.SLASH + SysContextHolder.getHeaderJsonStr(noConcurrent.headerArgs());
        log.info("key={}", key);
        return key;
    }

}