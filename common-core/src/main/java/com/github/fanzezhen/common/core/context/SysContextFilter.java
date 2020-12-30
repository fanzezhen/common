package com.github.fanzezhen.common.core.context;

import cn.hutool.core.util.StrUtil;
import com.github.fanzezhen.common.core.constant.SysConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * @author zezhen.fan
 */
@Slf4j
@Component
@Order(1)
public class SysContextFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            if (log.isDebugEnabled()) {
                log.debug("system context init");
            }
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String curHeader = headerNames.nextElement();
                if (StringUtils.startsWithIgnoreCase(curHeader, SysConstant.CONTEXT_HEADER_PREFIX)) {
                    String headerVal = request.getHeader(curHeader);
                    String requestUri = request.getRequestURI();
                    if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_USER_ID)) {
                        SysContext.put(SysConstant.HEADER_USER_ID, headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_USER_ID, headerVal);
                        }
                    } else if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_ACCOUNT_ID)) {
                        SysContext.put(SysConstant.HEADER_ACCOUNT_ID, headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_ACCOUNT_ID, headerVal);
                        }
                    } else if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_ACCOUNT_NAME)) {
                        headerVal = URLDecoder.decode(headerVal, StandardCharsets.UTF_8);
                        SysContext.put(SysConstant.HEADER_ACCOUNT_NAME, headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_ACCOUNT_NAME, headerVal);
                        }
                    } else if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_USER_NAME)) {
                        headerVal = URLDecoder.decode(headerVal, StandardCharsets.UTF_8);
                        SysContext.put(SysConstant.HEADER_USER_NAME, headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_USER_NAME, headerVal);
                        }
                    } else if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_USER_IP)) {
                        SysContext.put(SysConstant.HEADER_USER_IP, headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_USER_IP, headerVal);
                        }
                    } else if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_USER_AGENT)) {
                        SysContext.put(SysConstant.HEADER_USER_AGENT, headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_USER_AGENT, headerVal);
                        }
                    } else if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_TENANT_ID)) {
                        SysContext.put(SysConstant.HEADER_TENANT_ID, headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_TENANT_ID, headerVal);
                        }
                    } else if (StrUtil.equalsIgnoreCase(curHeader, SysConstant.HEADER_LOCALE)) {
                        SysContext.put(SysConstant.HEADER_LOCALE, headerVal);
                        if (log.isDebugEnabled()) {
                            log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_LOCALE, headerVal);
                        }
                    } else {
                        SysContext.put(curHeader, headerVal);
                    }
                }
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            SysContext.clean();
        }
    }

    @Override
    public void destroy() {
    }

}
