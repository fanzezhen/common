package com.github.fanzezhen.common.exception.exception;

import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import com.github.fanzezhen.common.core.util.HttpUtil;
import com.github.fanzezhen.common.exception.property.ResponseProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class DefaultExceptionResolver implements HandlerExceptionResolver {
    @Resource
    private ResponseProperty responseProperty;

    private View defaultErrorJsonView;

    private String defaultErrorPageView = "error/error";

    private Map<String, String> exceptionMappings;

    private int errorStatus = HttpServletResponse.SC_OK;

    private static final boolean fastJsonViewPresent = ClassUtils.isPresent(
            "com.alibaba.fastjson.support.spring.FastJsonJsonView", DefaultExceptionResolver.class.getClassLoader());

    public DefaultExceptionResolver() throws InstantiationException, IllegalAccessException, ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException {
        if (fastJsonViewPresent) {
            defaultErrorJsonView = (View) Class.forName(
                    "com.alibaba.fastjson.support.spring.FastJsonJsonView").getDeclaredConstructor()
                    .newInstance();
        } else {
            defaultErrorJsonView = new MappingJackson2JsonView();
        }
    }

    public void setDefaultErrorJsonView(View defaultErrorJsonView) {
        this.defaultErrorJsonView = defaultErrorJsonView;
    }

    public void setDefaultErrorPageView(String defaultErrorPageView) {
        this.defaultErrorPageView = defaultErrorPageView;
    }

    public void setExceptionMappings(Map<String, String> exceptionMappings) {
        this.exceptionMappings = exceptionMappings;
    }

    public void setErrorStatus(int errorStatus) {
        this.errorStatus = errorStatus;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response, Object handler, Exception ex) {
        log.error("请求" + request.getRequestURI() + "发生异常", ex);
        response.setStatus(errorStatus);
        ModelAndView modelAndView;
        if (responseProperty.jsonFlag) {
            return jsonResponse(request, ex);
        }
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 检查是否存在ResponseBody注解
            ResponseBody responseBody = handlerMethod
                    .getMethodAnnotation(ResponseBody.class);
            if (responseBody == null) {
                responseBody = AnnotationUtils.findAnnotation(
                        handlerMethod.getBeanType(), ResponseBody.class);
            }

            if (responseBody != null || HttpUtil.isAjaxRequest(request)) {
                // 判断是否存在jsonp注解
                modelAndView = jsonResponse(request, ex);
            } else {
                modelAndView = viewResponse(request, response, ex);
            }
        } else {
            // 其他类型handler
            // 判断同步请求还是异步请求
            if (HttpUtil.isAjaxRequest(request)) {
                modelAndView = jsonResponse(request, ex);
            } else {
                modelAndView = viewResponse(request, response, ex);
            }
        }
        return modelAndView;
    }

    private ModelAndView jsonResponse(HttpServletRequest request, Exception ex) {
        ModelAndView errorView = new ModelAndView();
        Map<String, Object> error = newExceptionResp(request, ex);
        errorView.setView(defaultErrorJsonView);
        errorView.addAllObjects(error);
        return errorView;
    }

    private ModelAndView viewResponse(HttpServletRequest request,
                                      HttpServletResponse response, Exception ex) {
        ModelAndView errorView = new ModelAndView();
        Map<String, Object> error = newExceptionResp(request, ex);
        String viewName = defaultErrorPageView;

        // 获取匹配的页面, 没有找到就使用默认errorPage
        if (exceptionMappings != null) {
            viewName = exceptionMappings.get(ex.getClass().getName());
            if (viewName == null) {
                viewName = defaultErrorPageView;
            }
        }

        errorView.setViewName(viewName);
        errorView.addAllObjects(error);
        return errorView;
    }

    public Map<String, Object> newExceptionResp(HttpServletRequest request, Exception exception) {
        Map<String, Object> error = new HashMap<String, Object>();
        if (exception instanceof ServiceException) {
            Integer errCode = ((ServiceException) exception).getCode();
            error.put("msg", exception.getMessage());
            error.put("code", errCode);
        } else if (exception instanceof IllegalArgumentException) {
            error.put("msg", exception.getMessage());
            error.put("code", CoreExceptionEnum.SERVICE_ERROR.getCode());
        } else {
            error.put("msg", CoreExceptionEnum.SERVICE_ERROR.getMessage());
            error.put("code", CoreExceptionEnum.SERVICE_ERROR.getCode());
        }
        return error;
    }
}
