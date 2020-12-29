package com.github.fanzezhen.common.core.mvc;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.github.fanzezhen.common.core.constant.CommonConstant;
import com.github.fanzezhen.common.core.model.response.ActionResult;
import com.google.common.collect.Sets;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;


/**
 * @author zhujiajun
 * @version 1.0
 * @since 2019-04-15 16:23
 */
public class ResponseBodyWrapHandler implements HandlerMethodReturnValueHandler {

    private final HandlerMethodReturnValueHandler delegate;
    private final Set<String> autoWrapResponseIgnoreUrlSet;

    public ResponseBodyWrapHandler(HandlerMethodReturnValueHandler delegate, String autoWrapResponseIgnoreUrls) {
        this.delegate = delegate;
        this.autoWrapResponseIgnoreUrlSet = Sets.newHashSet(autoWrapResponseIgnoreUrls.split(","));
    }

    @Override
    public boolean supportsReturnType(@NonNull MethodParameter returnType) {
        return delegate.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(@Nullable Object returnValue,
                                  @NonNull MethodParameter returnType,
                                  @NonNull ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest) throws Exception {
        if (returnValue == null) {
            delegate.handleReturnValue(new ActionResult<>(), returnType, mavContainer, webRequest);
        } else {
            if (returnValue instanceof ActionResult) {
                delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
            } else {
                HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
                // 以下代码已兼容requestUri为空的情况，所以忽略空指针警告
                @SuppressWarnings("all")
                String requestUri = request.getRequestURI();
                if (StrUtil.contains(requestUri, CommonConstant.SEPARATOR_URL)) {
                    requestUri = requestUri.substring(0, requestUri.indexOf("?"));
                }
                // 对特殊的URL不进行统一包装结果处理
                if (CollectionUtil.contains(autoWrapResponseIgnoreUrlSet, requestUri)) {
                    delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
                } else {
                    delegate.handleReturnValue(ActionResult.success(returnValue), returnType, mavContainer, webRequest);
                }
            }
        }
    }
}
