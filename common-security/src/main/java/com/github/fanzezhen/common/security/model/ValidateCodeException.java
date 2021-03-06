package com.github.fanzezhen.common.security.model;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码异常
 * @author zezhen.fan
 */
public class ValidateCodeException extends AuthenticationException {

    private static final long serialVersionUID = 2672899097153524723L;

    public ValidateCodeException(String explanation) {
        super(explanation);
    }
}
