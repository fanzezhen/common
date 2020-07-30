package com.github.fanzezhen.common.security.facade.feign.remote;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

//@FeignClient(url = "${security.sys.remote}", name = "securitySysRoleRemote")
@Deprecated
public interface SysRoleRemote {
    @GetMapping("/user/getUserByName")
    List<Map<String, Object>> listMapByUserId(@RequestParam("userId") String userId, @RequestParam("username") String appCode);
    @GetMapping("/user/getUserByName")
    List<Map<String, Object>> listMapByUserId(@RequestParam("userId") String userId);
}
