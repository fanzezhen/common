package com.github.fanzezhen.common.security.util;

import cn.hutool.crypto.digest.BCrypt;
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
     * 认证通过或已记住的用户。与guest搭配使用。
     *
     * @return 用户：true，否则 false
     */
    public static boolean isLoginUser() {
        return SecurityContextHolder.getContext() != null && getAuthenticationNotNull() != null;
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
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return authentication;
    }

    /**
     * 获取认证信息
     */
    public static Authentication getAuthenticationNotNull() {
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
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;
        //details里面可能存放了当前登录用户的详细信息，也可以通过cast后拿到
        User userDetails = (User) authenticationToken.getPrincipal();
        if (userDetails == null) {
            throw new ServiceException(CoreExceptionEnum.NO_CURRENT_USER);
        }
        return userDetails;
    }

    /**
     * 获取登录用户
     */
    public static SysUserDetail getSysUserDetail() {
        Authentication authentication = getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        return (SysUserDetail) authentication.getPrincipal();
    }

    /**
     * 获取登录用户
     */
    public static SysUserDetail getSysUserDetailNotNull() {
        try {
            return (SysUserDetail) getAuthenticationNotNull().getPrincipal();
        } catch (Exception e) {
            throw new ServiceException(CoreExceptionEnum.NO_CURRENT_USER);
        }
    }

    /**
     * 获取登录用户
     */
    public static String getSysUserId() {
        try {
            return getSysUserDetailNotNull().getId();
        } catch (Exception e) {
            throw new ServiceException(CoreExceptionEnum.NO_CURRENT_USER);
        }
    }

    /**
     * 获取登录用户
     */
    public static String getLoginUserId() {
        SysUserDetail sysUserDetail = getSysUserDetail();
        if (sysUserDetail == null) {
            return null;
        }
        return sysUserDetail.getId();
    }

    /**
     * 获取登录用户
     */
    public static String getLoginUsername() {
        SysUserDetail sysUserDetail = getSysUserDetail();
        if (sysUserDetail == null) {
            return null;
        }
        return sysUserDetail.getUsername();
    }

    /**
     * 获取登录用户
     */
    public static String getLoginUserIdNotNull() {
        try {
            return getSysUserDetailNotNull().getId();
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
        System.out.println(BCrypt.hashpw("111111"));
        System.out.println(BCrypt.checkpw("111111", "$2a$10$q5uDd14HgRnRFmRB5u/f4Oo/.mvP0bfnNnnwqC7iuwUZRw4gs1OTq"));
        System.out.println(encrypt("111111"));
        System.out.println(BCrypt.checkpw("111111", "$2a$10$udn72Xru23wSzSVUo7zUbOuus.sb50GRueNCKWaIVfbckkNgMmwWm"));
        System.out.println(encrypt("111111"));
        System.out.println(BCrypt.checkpw("111111", "$2a$10$yL.BdfDenp2uqQm3U86PjulKCFScpbL9vO7Ml4r6TEaUev/2jqmxi"));
    }
}
