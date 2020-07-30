package com.github.fanzezhen.common.security.facade;

import com.github.fanzezhen.common.core.model.dto.SysPermissionDto;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserDetailsServiceFacade extends UserDetailsService, AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
    List<SysPermissionDto> listAllPermissionDto(String appCode);
}
