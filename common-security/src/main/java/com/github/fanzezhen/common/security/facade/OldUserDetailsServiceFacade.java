package com.github.fanzezhen.common.security.facade;

import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Deprecated
public interface OldUserDetailsServiceFacade extends UserDetailsService, AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
    List<Map<String, Object>> listMap(String appCode);
    List<Map<String, Object>> listMap();;
}
