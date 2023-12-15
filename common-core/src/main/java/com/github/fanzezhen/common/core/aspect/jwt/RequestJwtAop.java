package com.github.fanzezhen.common.core.aspect.jwt;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.jwt.JWT;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.fanzezhen.common.core.context.SysContextHolder;
import com.github.fanzezhen.common.core.service.CacheService;
import com.github.fanzezhen.common.core.util.ExceptionUtil;
import com.github.fanzezhen.common.core.util.SortUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zezhen.fan
 */
@Slf4j
@Aspect
@Component
public class RequestJwtAop {
    @Value("${jwt.account.secret.json:'{\n" +
            "    \"taimei\": {\n" +
            "        \"secret\": \"Taimei@123\",\n" +
            "        \"tenantId\": \"\"\n" +
            "    }" +
            "}'}")
    private String jwtSecretJson;

    /**
     * 要处理的方法，包名+类名+方法名
     */
    @Pointcut("@annotation(com.github.fanzezhen.common.core.aspect.jwt.JwtVerify)")
    public void cut() {
    }

    /**
     * 在调用上面 @Pointcut标注的方法前执行以下方法
     *
     * @param joinPoint JoinPoint
     */
    @Before("cut()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        List<Object> argList = new ArrayList<>(joinPoint.getArgs().length);
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                continue;
            }
            if (arg instanceof MultipartFile) {
                argList.add(((MultipartFile) arg).getName());
                continue;
            }
            argList.add(arg);
        }
        String args = JSON.toJSONString(argList);
        log.info("验证JWT： url={}, Args={}", request.getRequestURL().toString(), args);
        JwtVerify jwtVerify = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(JwtVerify.class);
        String token = SysContextHolder.get(jwtVerify.header());
        ExceptionUtil.throwIfBlank(token, "token", "不能为空");
        CacheService cacheService = SortUtil.getFirstByOrder(SpringUtil.getBeansOfType(CacheService.class).values());
        ExceptionUtil.throwIf(cacheService == null, "缓存未注入");
        String account = cacheService.get(SysContextHolder.getAppId() + SysContextHolder.getTenantId() + "-jwt-" + token);
        ExceptionUtil.throwIfBlank(account, "", "token is null or not exists");
        JSONObject jwtSecretJsonObj = JSONObject.parseObject(jwtSecretJson);
        JSONObject authInfoJson = null;
        try {
            authInfoJson = jwtSecretJsonObj.getJSONObject(account);
        } catch (Throwable throwable) {
            log.warn("{}", jwtSecretJsonObj.get(account), throwable);
        }
        ExceptionUtil.throwIfBlank(authInfoJson, "", "账户不存在");
        String secret = authInfoJson.getString("secret");
        ExceptionUtil.throwIfBlank(secret, "", account + "应用秘钥未发布");
        // 默认验证HS265的算法
        ExceptionUtil.throwIf(!JWT.of(token).setKey(secret.getBytes(StandardCharsets.UTF_8)).verify(), 405, "token验证失败");
        String tenantId = authInfoJson.getString("tenantId");
        if (CharSequenceUtil.isNotEmpty(tenantId)) {
            SysContextHolder.setTenantId(tenantId);
        }
        log.info("验证JWT通过： url={}, Args={}", request.getRequestURL().toString(), args);
    }

}
