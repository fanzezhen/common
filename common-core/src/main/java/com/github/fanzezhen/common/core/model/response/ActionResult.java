package com.github.fanzezhen.common.core.model.response;

import cn.hutool.core.collection.CollectionUtil;
import cn.stylefeng.roses.kernel.model.exception.AbstractBaseExceptionEnum;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.validation.FieldError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 响应信息主体
 *
 * @param <T>
 * @author zezhen.fan
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private T data;
    private List<ErrorInfo> errors;

    public ActionResult(boolean success) {
        this.success = success;
    }

    public ActionResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public ActionResult(List<ErrorInfo> errors) {
        this.success = false;
        this.errors = errors;
    }

    public ActionResult(ErrorInfo error) {
        this.success = false;
        this.errors = List.of(error);
    }

    public ActionResult(boolean success, T data, ErrorInfo error) {
        this.success = success;
        this.data = data;
        this.errors = List.of(error);
    }

    public static <T> ActionResult<T> success() {
        return new ActionResult<>(true);
    }

    public static <T> ActionResult<T> success(T data) {
        return new ActionResult<>(true, data);
    }

    public static <T> ActionResult<T> failed() {
        return new ActionResult<>(false);
    }

    public static <T> ActionResult<T> failed(String msg) {
        return new ActionResult<>(new ErrorInfo(msg));
    }

    public static <T> ActionResult<T> failed(ErrorInfo errorInfo) {
        return new ActionResult<>(errorInfo);
    }

    public static <T> ActionResult<T> failed(List<FieldError> fieldErrors) {
        if (CollectionUtil.isEmpty(fieldErrors)) {
            return failed();
        }
        List<ErrorInfo> errorList = new ArrayList<>();
        fieldErrors.forEach(fieldError -> errorList.add(new ErrorInfo(String.valueOf(fieldError))));
        return new ActionResult<>(errorList);
    }

    public static <T> ActionResult<T> failed(AbstractBaseExceptionEnum exceptionEnum) {
        return new ActionResult<>(new ErrorInfo(exceptionEnum));
    }

    public static <T> ActionResult<T> failed(ServiceException serviceException) {
        return new ActionResult<>(new ErrorInfo(serviceException));
    }

    public static <T> ActionResult<T> failed(T data) {
        return new ActionResult<>(false, data);
    }

    public static <T> ActionResult<T> failed(T data, String msg) {
        return new ActionResult<>(false, data, new ErrorInfo(msg));
    }
}

