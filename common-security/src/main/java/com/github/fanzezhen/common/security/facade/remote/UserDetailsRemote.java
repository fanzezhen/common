package com.github.fanzezhen.common.security.facade.remote;

import com.github.fanzezhen.common.core.model.dto.SysPermissionDto;
import com.github.fanzezhen.common.core.model.dto.SysUserDto;
import com.github.fanzezhen.common.core.model.response.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author zezhen.fan
 */
@FeignClient(url = "${security.remote.user.detail.url}", name = "userDetailsRemote")
public interface UserDetailsRemote {

    @GetMapping("/user/by/username")
    R<SysUserDto> loadUserByUsername(@RequestParam("username") String username, @RequestParam("appCode") String appCode);

    @GetMapping("/permission/list")
    R<List<SysPermissionDto>> listPermission(@RequestParam("appCode") String appCode);
}
