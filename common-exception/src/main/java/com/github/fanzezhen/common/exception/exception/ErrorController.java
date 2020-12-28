package com.github.fanzezhen.common.exception.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zezhen.fan
 */
@Slf4j
@Controller
@RequestMapping("/error")
public class ErrorController {
    /**
     * 错误页面
     */
    @RequestMapping(value = "/error/{errorCode}")
    public String error(@PathVariable String errorCode) {
        return "error/" + errorCode;
    }
}
