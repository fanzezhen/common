package com.github.fanzezhen.common.core.aspect.concurrent;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
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
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static java.util.stream.Collectors.toList;

/**
 * @author zezhen.fan
 */
@Slf4j
@Aspect
@Component
public class NoConcurrentAop {
    private final String springApplicationName;
    /**
     * 环境隔离变量
     */
    private final String env;
    private final LockService lockService;

    @Autowired(required = false)
    public NoConcurrentAop(String springApplicationName, String env, LockService lockService) {
        this.springApplicationName = springApplicationName;
        this.env = env;
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
        if (CharSequenceUtil.isEmpty(paramKey)) {
            paramKey = JSON.toJSONString(Arrays.stream(args).filter(arg -> !(arg instanceof HttpServletRequest)).toList());
        }
        String key = env + StrPool.SLASH +
                springApplicationName + StrPool.SLASH +
                "NoConcurrent" + StrPool.SLASH +
                joinPoint.getTarget().getClass().getName() + StrPool.DOT + joinPoint.getSignature().getName() + StrPool.SLASH +
                paramKey + StrPool.SLASH + SysContextHolder.getHeaderJsonStr(noConcurrent.headerArgs());
        log.info("key={}", key);
        return key;
    }

}
