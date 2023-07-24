package com.github.fanzezhen.common.exception.exception;

import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import com.github.fanzezhen.common.core.property.CommonProperty;
import com.github.fanzezhen.common.core.util.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理
 *
 * @author zezhen.fan
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class DefaultExceptionResolver implements HandlerExceptionResolver {
    @Resource
    private CommonProperty responseProperty;

    private final View defaultErrorJsonView;

    private static final boolean FAST_JSON_VIEW_PRESENT = ClassUtils.isPresent(
            "com.alibaba.fastjson.support.spring.FastJsonJsonView", DefaultExceptionResolver.class.getClassLoader());

    public DefaultExceptionResolver() throws InstantiationException, IllegalAccessException, ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException {
        if (FAST_JSON_VIEW_PRESENT) {
            defaultErrorJsonView = (View) Class.forName(
                    "com.alibaba.fastjson.support.spring.FastJsonJsonView").getDeclaredConstructor()
                    .newInstance();
        } else {
            defaultErrorJsonView = new MappingJackson2JsonView();
        }
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        log.error("请求" + request.getRequestURI() + "发生异常", ex);
        int errorStatus = HttpServletResponse.SC_OK;
        response.setStatus(errorStatus);
        ModelAndView modelAndView;
        if (responseProperty.isResponseJson()) {
            return jsonResponse(ex);
        }
        if (handler instanceof HandlerMethod handlerMethod) {
            // 检查是否存在ResponseBody注解
            ResponseBody responseBody = handlerMethod.getMethodAnnotation(ResponseBody.class);
            if (responseBody == null) {
                responseBody = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), ResponseBody.class);
            }
            if (responseBody != null || HttpUtil.isAjaxRequest(request)) {
                // 判断是否存在jsonp注解
                modelAndView = jsonResponse(ex);
            } else {
                modelAndView = viewResponse(response, ex);
            }
        } else {
            // 其他类型handler
            // 判断同步请求还是异步请求
            if (HttpUtil.isAjaxRequest(request)) {
                modelAndView = jsonResponse(ex);
            } else {
                modelAndView = viewResponse(response, ex);
            }
        }
        return modelAndView;
    }

    private ModelAndView jsonResponse(Exception ex) {
        ModelAndView errorView = new ModelAndView();
        Map<String, Object> error = newExceptionResp(ex);
        errorView.setView(defaultErrorJsonView);
        errorView.addAllObjects(error);
        return errorView;
    }

    private ModelAndView viewResponse(HttpServletResponse response, Exception ex) {
        ModelAndView errorView = new ModelAndView();
        Map<String, Object> error = newExceptionResp(ex);
        errorView.setViewName("redirect:/error/" + response.getStatus());
        errorView.addAllObjects(error);
        return errorView;
    }

    public Map<String, Object> newExceptionResp(Exception exception) {
        Map<String, Object> error = new HashMap<>(2);
        if (exception instanceof ServiceException serviceException) {
            error.put("msg", serviceException.getErrorMessage());
            error.put("code", serviceException.getCode());
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
