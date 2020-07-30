package com.github.fanzezhen.common.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Slf4j
@Controller
@RequestMapping("/oauth")
public class OAuthController {
    @GetMapping("/login")
    public String login(ModelMap modelMap, HttpServletRequest request) {
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            modelMap.addAttribute(paramName, request.getParameter(paramName));
        }
        return "login";
    }
}
