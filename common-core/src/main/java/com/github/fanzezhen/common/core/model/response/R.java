package com.github.fanzezhen.common.core.model.response;

import cn.stylefeng.roses.kernel.model.exception.AbstractBaseExceptionEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Objects;


/**
 * 响应信息主体
 *
 * @param <T>
 * @author lengleng
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true) // 忽略ok、success等字段
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private Integer code;

    @Getter
    @Setter
    private String message;


    @Getter
    @Setter
    private T data;

    public static <T> R<T> ok() {
        return restResult(null, HttpStatus.OK.value(), "success");
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, HttpStatus.OK.value(), "success");
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, HttpStatus.OK.value(), msg);
    }

    public static <T> R<T> failed() {
        return restResult(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
    }

    public static <T> R<T> failed(String msg) {
        return restResult(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    public static <T> R<T> failed(AbstractBaseExceptionEnum msg) {
        return restResult(null, msg.getCode(), msg.getMessage());
    }

    public static <T> R<T> failed(T data) {
        return restResult(data, HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
    }

    public static <T> R<T> failed(T data, String msg) {
        return restResult(data, HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    private static <T> R<T> restResult(T data, Integer code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMessage(msg);
        return apiResult;
    }

    public boolean isOk() {
        return Objects.equals(code, HttpStatus.OK.value());
    }

    public boolean isSuccess() {
        return Objects.equals(code, HttpStatus.OK.value());
    }
}

