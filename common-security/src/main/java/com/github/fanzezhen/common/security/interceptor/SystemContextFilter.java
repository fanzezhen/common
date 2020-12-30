package com.github.fanzezhen.common.security.interceptor;

import com.github.fanzezhen.common.core.constant.SysConstant;
import com.github.fanzezhen.common.core.context.SysContext;
import com.github.fanzezhen.common.security.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zezhen.fan
 */
@Slf4j
@Component
public class SystemContextFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        try {
            if (log.isDebugEnabled()) {
                log.debug("system context init");
            }
            String requestUri = request.getRequestURI();
            SysContext.put(SysConstant.HEADER_USER_ID, SecurityUtil.getLoginUserId());
            if (log.isDebugEnabled()) {
                log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_USER_ID, SecurityUtil.getLoginUserId());
            }
            SysContext.put(SysConstant.HEADER_USER_NAME, SecurityUtil.getLoginUsername());
            if (log.isDebugEnabled()) {
                log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_USER_NAME, SecurityUtil.getLoginUsername());
            }
            String realIp = request.getHeader(SysConstant.HEADER_NGINX_REAL_IP);
            SysContext.put(SysConstant.HEADER_USER_IP, realIp);
            SysContext.put(SysConstant.HEADER_NGINX_REAL_IP, realIp);
            String userAgent = request.getHeader(SysConstant.HEADER_USER_AGENT);
            SysContext.put(SysConstant.HEADER_USER_AGENT, userAgent);
            if (log.isDebugEnabled()) {
                log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_NGINX_REAL_IP, realIp);
                log.debug("request {} has  header: {}, with value {}", requestUri, SysConstant.HEADER_USER_AGENT, userAgent);
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
