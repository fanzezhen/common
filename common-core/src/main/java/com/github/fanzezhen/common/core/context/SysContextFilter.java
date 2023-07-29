package com.github.fanzezhen.common.core.context;

import cn.hutool.core.util.StrUtil;
import com.github.fanzezhen.common.core.constant.SysConstant;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * @author zezhen.fan
 */
@Slf4j
@WebFilter
@Order(SysConstant.FILTER_ORDER)
public class SysContextFilter implements Filter, Ordered {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            if (log.isDebugEnabled()) {
                log.debug("system context init");
            }
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String curHeader = headerNames.nextElement();
                if (StrUtil.startWithIgnoreCase(curHeader, SysConstant.CONTEXT_HEADER_PREFIX)) {
                    String headerVal = request.getHeader(curHeader);
                    String requestUri = request.getRequestURI();
                    if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_USER_ID)) {
                        SysContextHolder.setUserId(headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_USER_ID, headerVal);
                        }
                    } else if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_ACCOUNT_ID)) {
                        SysContextHolder.set(SysConstant.HEADER_ACCOUNT_ID, headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_ACCOUNT_ID, headerVal);
                        }
                    } else if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_ACCOUNT_NAME)) {
                        headerVal = URLDecoder.decode(headerVal, StandardCharsets.UTF_8);
                        SysContextHolder.set(SysConstant.HEADER_ACCOUNT_NAME, headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_ACCOUNT_NAME, headerVal);
                        }
                    } else if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_USER_NAME)) {
                        headerVal = URLDecoder.decode(headerVal, StandardCharsets.UTF_8);
                        SysContextHolder.set(SysConstant.HEADER_USER_NAME, headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_USER_NAME, headerVal);
                        }
                    } else if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_USER_IP)) {
                        SysContextHolder.set(SysConstant.HEADER_USER_IP, headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_USER_IP, headerVal);
                        }
                    } else if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_USER_AGENT)) {
                        SysContextHolder.set(SysConstant.HEADER_USER_AGENT, headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_USER_AGENT, headerVal);
                        }
                    } else if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_TENANT_ID)) {
                        SysContextHolder.set(SysConstant.HEADER_TENANT_ID, headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_TENANT_ID, headerVal);
                        }
                    } else if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_LOCALE)) {
                        SysContextHolder.set(SysConstant.HEADER_LOCALE, headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_LOCALE, headerVal);
                        }
                    } else if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_APP_CODE)) {
                        SysContextHolder.set(SysConstant.HEADER_APP_CODE, headerVal);
                        if (log.isDebugEnabled()) {
                            if (log.isDebugEnabled()) {
                                log.debug("request {} has header: {}, with value {}", requestUri, curHeader, headerVal);
                            }
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_APP_CODE, headerVal);
                        }
                    } else {
                        SysContextHolder.set(curHeader, headerVal);
                    }
                }
            }
            chain.doFilter(servletRequest, servletResponse);
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            SysContextHolder.clean();
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
