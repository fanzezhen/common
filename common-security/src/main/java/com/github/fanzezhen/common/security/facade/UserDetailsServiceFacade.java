package com.github.fanzezhen.common.security.facade;

import com.github.fanzezhen.common.mp.model.dto.SysPermissionDto;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zezhen.fan
 */
@Service
public interface UserDetailsServiceFacade extends UserDetailsService, AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
    /**
     * 权限列表
     *
     * @param appCode APP标识
     * @return 权限列表
     */
    List<SysPermissionDto> listAllPermissionDto(String appCode);
}
