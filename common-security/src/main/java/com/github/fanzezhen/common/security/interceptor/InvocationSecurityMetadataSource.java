package com.github.fanzezhen.common.security.interceptor;

import com.github.fanzezhen.common.core.constant.SecurityConstant;
import com.github.fanzezhen.common.mp.model.dto.SysPermissionDto;
import com.github.fanzezhen.common.security.facade.UserDetailsServiceFacade;
import com.github.fanzezhen.common.core.property.CommonProjectProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @author fanzezhen
 * @date 17/1/19
 */
@Component
public class InvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    @Resource
    private CommonProjectProperties commonProjectProperties;
    @Resource
    private UserDetailsServiceFacade userDetailsServiceFacade;

    private HashMap<String, Collection<ConfigAttribute>> map;

    /**
     * 加载权限表中所有权限
     */
    public void loadResourceDefine() {
        map = new HashMap<>(100);
        Collection<ConfigAttribute> array;
        ConfigAttribute cfg;
        List<SysPermissionDto> sysPermissionDtoList = userDetailsServiceFacade.listAllPermissionDto(commonProjectProperties.getAppCode());
        for (SysPermissionDto sysPermissionDto : sysPermissionDtoList) {
            array = new ArrayList<>();
            cfg = new SecurityConfig(SecurityConstant.PERMISSION_PREFIX + sysPermissionDto.getId());
            array.add(cfg);
            //用权限的getUrl() 作为map的key，用ConfigAttribute的集合作为 value，
            map.put(String.valueOf(sysPermissionDto.getOperationUrl()), array);
        }
    }

    /**
     * 此方法是为了判定用户请求的url 是否在权限表中，如果在权限表中，则返回给 decide 方法，用来判定用户是否有此权限。如果不在权限表中则放行。
     *
     * @param object object
     * @return Collection<ConfigAttribute>
     * @throws IllegalArgumentException IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        if (map == null) {
            loadResourceDefine();
        }
        //object 中包含用户请求的request 信息
        HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
        AntPathRequestMatcher matcher;
        String resUrl;
        for (String s : map.keySet()) {
            resUrl = s;
            matcher = new AntPathRequestMatcher(resUrl);
            if (matcher.matches(request)) {
                return map.get(resUrl);
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
