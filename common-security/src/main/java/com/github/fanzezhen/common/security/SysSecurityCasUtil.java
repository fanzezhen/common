package com.github.fanzezhen.common.security;

import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class SysSecurityCasUtil {
    /**
     * 获取登录用户
     *
     * @return
     */
    public static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication;
        } else {
            throw new ServiceException(CoreExceptionEnum.NO_CURRENT_USER);
        }
    }

    /**
     * 获取登录用户
     *
     * @return
     */
    public static User getUser() {
        try {
            return (User) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new ServiceException(CoreExceptionEnum.NO_CURRENT_USER);
        }
    }
}
