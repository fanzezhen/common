package com.github.fanzezhen.common.exception.exception;

import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import com.github.fanzezhen.common.core.model.response.ActionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;

/**
 * 全局的的异常处理器
 *
 * @author zezhen.fan
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 全局异常.
     *
     * @param e the e
     * @return R
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> exception(Exception e) {
        log.error("全局异常信息 ex={}", e.getMessage(), e);
        return ActionResult.failed(CoreExceptionEnum.SERVICE_ERROR);
    }

    @ExceptionHandler(value = {NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> noHandlerFoundException(Exception e, HttpServletRequest request) {
        log.error("request:{} Method:{} message:{}", request.getRequestURI(), request.getMethod(), e.getMessage(), e);
        return ActionResult.failed("unhandled  server exception", request.getRequestURI());
    }

    /**
     * validation exception
     *
     * @param exception 异常
     * @return R
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> bodyValidExceptionHandler(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        log.error("validation exception", exception);
        return ActionResult.failed(fieldErrors);
    }

    /**
     * validation exception
     *
     * @param exception 异常
     * @return R
     */
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> bodyValidExceptionHandler(ConstraintViolationException exception) {
        log.error("validation exception：{}", exception.getMessage(), exception);
        return ActionResult.failed(exception);
    }

    /**
     * validation exception
     *
     * @param exception 异常
     * @return R
     */
    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> bodyValidExceptionHandler(ValidationException exception) {
        log.error("validation exception：{}", exception.getMessage(), exception);
        return ActionResult.failed(exception.getMessage());
    }

    @ExceptionHandler(value = {ServiceException.class})
    @ResponseStatus(HttpStatus.OK)
    public ActionResult<Object> businessException(ServiceException e, HttpServletRequest request) {
        log.error("request:{} Method:{} message:{}", request.getRequestURI(), request.getMethod(), e.getMessage(), e);
        return ActionResult.failed(e);
    }
}
