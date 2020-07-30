package com.github.fanzezhen.common.security.facade.feign.remote;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

//@FeignClient(url = "${security.sys.remote}", name = "securitySysUserRemote")
@Deprecated
public interface SysUserRemote {
    @GetMapping("/user/getUserByName")
    Map<String, Object> loadUserByUsername(@RequestParam("username") String username, @RequestParam("username") String appCode);
    @GetMapping("/user/getUserByName")
    Map<String, Object> loadUserByUsername(@RequestParam("username") String username);
}
