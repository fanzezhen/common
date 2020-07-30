package com.github.fanzezhen.common.security.facade.feign.remote;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

//@FeignClient(url = "${security.sys.remote}", name = "securitySysPermissionRemote")
@Deprecated
public interface SysPermissionRemote {
    @GetMapping("/user/getUserByName")
    List<String> listIdByRoleIds(@RequestParam("roleIds") Collection<String> roleIds, @RequestParam("username") String appCode);
    @GetMapping("/user/getUserByName")
    List<String> listIdByRoleIds(@RequestParam("roleIds") Collection<String> roleIds);
    @GetMapping("/user/getUserByName")
    List<String> listId();

    @PostMapping("/list/map")
    List<Map<String, Object>> listMap();
    @PostMapping("/list/map/by/app-code")
    List<Map<String, Object>> listMapByAppCode(@RequestParam("appCode") String appCode);
}
