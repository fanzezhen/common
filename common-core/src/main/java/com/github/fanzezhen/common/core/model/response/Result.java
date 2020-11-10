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
 * JsonIgnoreProperties注解用于忽略ok、success等字段
 *
 * @param <T>
 * @author zezhen.fan
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result<T> implements Serializable {
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

    public static <T> Result<T> ok() {
        return restResult(null, HttpStatus.OK.value(), "success");
    }

    public static <T> Result<T> ok(T data) {
        return restResult(data, HttpStatus.OK.value(), "success");
    }

    public static <T> Result<T> ok(T data, String msg) {
        return restResult(data, HttpStatus.OK.value(), msg);
    }

    public static <T> Result<T> failed() {
        return restResult(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
    }

    public static <T> Result<T> failed(String msg) {
        return restResult(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    public static <T> Result<T> failed(AbstractBaseExceptionEnum msg) {
        return restResult(null, msg.getCode(), msg.getMessage());
    }

    public static <T> Result<T> failed(T data) {
        return restResult(data, HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
    }

    public static <T> Result<T> failed(T data, String msg) {
        return restResult(data, HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    private static <T> Result<T> restResult(T data, Integer code, String msg) {
        Result<T> apiResult = new Result<>();
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

