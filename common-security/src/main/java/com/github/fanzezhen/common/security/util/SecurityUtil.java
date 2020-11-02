package com.github.fanzezhen.common.security.util;

import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import com.github.fanzezhen.common.security.model.SysUserDetail;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author zezhen.fan
 */
public class SecurityUtil {
    /**
     * 认证通过或已记住的用户。与guset搭配使用。
     *
     * @return 用户：true，否则 false
     */
    public static boolean isLoginUser() {
        return SecurityContextHolder.getContext() != null && getAuthentication() != null;
    }

    /**
     * 验证当前用户是否为“访客”，即未认证（包含未记住）的用户。用user搭配使用
     *
     * @return 访客：true，否则false
     */
    public static boolean isGuest() {
        return !isLoginUser();
    }

    /**
     * 获取认证信息
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
     */
    public static User getUser() {
        if (getAuthentication() == null) return null;
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) getAuthentication();
        //details里面可能存放了当前登录用户的详细信息，也可以通过cast后拿到
        User userDetails = (User) authenticationToken.getPrincipal();
        if (userDetails == null) throw new ServiceException(CoreExceptionEnum.NO_CURRENT_USER);
        return userDetails;
    }

    /**
     * 获取登录用户
     */
    public static SysUserDetail getSysUserDetail() {
        try {
            return (SysUserDetail) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new ServiceException(CoreExceptionEnum.NO_CURRENT_USER);
        }
    }

    /**
     * 获取登录用户
     */
    public static String getLoginUserId() {
        try {
            return getSysUserDetail().getId();
        } catch (Exception e) {
            throw new ServiceException(CoreExceptionEnum.NO_CURRENT_USER);
        }
    }

    public static String encrypt(String s) {
        return encrypt(s, new BCryptPasswordEncoder());
    }

    public static String encrypt(String s, BCryptPasswordEncoder encoder) {
        return encoder.encode(s);
    }

    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        return verifyPassword(rawPassword, encodedPassword, new BCryptPasswordEncoder());
    }

    public static boolean verifyPassword(String rawPassword, String encodedPassword, BCryptPasswordEncoder encoder) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(encrypt("111111"));
        }
    }
}
