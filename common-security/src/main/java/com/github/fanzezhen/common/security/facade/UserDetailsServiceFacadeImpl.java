package com.github.fanzezhen.common.security.facade;

import com.alibaba.fastjson.JSON;
import com.github.fanzezhen.common.core.constant.CacheConstants;
import com.github.fanzezhen.common.core.constant.SecurityConstant;
import com.github.fanzezhen.common.core.enums.auth.RoleEnum;
import com.github.fanzezhen.common.core.model.dto.SysPermissionDto;
import com.github.fanzezhen.common.core.model.dto.SysUserDto;
import com.github.fanzezhen.common.core.model.response.R;
import com.github.fanzezhen.common.security.facade.remote.UserDetailsRemote;
import com.github.fanzezhen.common.security.model.SysUserDetail;
import com.github.fanzezhen.common.security.property.SecurityProjectProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserDetailsServiceFacadeImpl implements UserDetailsServiceFacade {
    @Resource
    private SecurityProjectProperty securityProjectProperty;
    @Resource
    private UserDetailsRemote userDetailsRemote;

    @Override
    public UserDetails loadUserDetails(CasAssertionAuthenticationToken casAssertionAuthenticationToken) throws UsernameNotFoundException {
        // 结合具体的逻辑去实现用户认证，并返回继承UserDetails的用户对象;
        String username = casAssertionAuthenticationToken.getName();
        log.info("当前的用户名是：" + username);

        SysUserDetail userInfo = loadUserByUsername(username);
        log.info(userInfo.toString());
        return userInfo;
    }

    @Override
//    @Cacheable(value = CacheConstants.USER_DETAILS, key = "#username")
    public SysUserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
        //用户，用于判断权限，请注意此用户名和方法参数中的username一致；BCryptPasswordEncoder是用来演示加密使用。
        SysUserDto sysUserDto = userDetailsRemote.loadUserByUsername(username, securityProjectProperty.APP_CODE).getData();
        if (sysUserDto != null && StringUtils.isNotBlank(sysUserDto.getUsername())) {
            //生成环境是查询数据库获取username的角色用于后续权限判断（如：张三 admin)
            Set<GrantedAuthority> grantedAuthorities;
            Set<String> grantedAuthorityNameSet = new HashSet<>();
            if (!CollectionUtils.sizeIsEmpty(sysUserDto.getRoleTypeSets())) {
                for (SysPermissionDto sysPermissionDto :
                        sysUserDto.getRoleTypeSets().contains(RoleEnum.RoleTypeEnum.SPECIAL_ADMIN.getType()) ?   // 超级管理员拥有所有权限
                                userDetailsRemote.listPermission(securityProjectProperty.APP_CODE).getData() :
                                sysUserDto.getSysPermissionDtoList()) {
                    grantedAuthorityNameSet.add(SecurityConstant.PERMISSION_PREFIX + sysPermissionDto.getId());
                }
            }
            grantedAuthorityNameSet.addAll(RoleEnum.RoleTypeEnum.securityRoleTypeCodeSetByType(sysUserDto.getRoleTypeSets()));
            //1：此处将权限信息添加到 GrantedAuthority 对象中，在后面进行全权限验证时会使用GrantedAuthority 对象。
            grantedAuthorities = new HashSet<>(AuthorityUtils.commaSeparatedStringToAuthorityList(
                    String.join(",", grantedAuthorityNameSet)));
            SysUserDetail sysUserDetail = new SysUserDetail(sysUserDto, grantedAuthorities);
            sysUserDetail.setRoleIds(sysUserDto.getRoleIdSets());
            sysUserDetail.setRoleNames(sysUserDto.getRoleNameSets());
            sysUserDetail.setRoleTypes(sysUserDto.getRoleTypeSets());
            return sysUserDetail;
        } else {
            throw new UsernameNotFoundException("user: " + username + " do not exist!");
        }

    }

    @Override
    @Cacheable(value = CacheConstants.PERMISSION_DETAILS, key = "#appCode")
    public List<SysPermissionDto> listAllPermissionDto(String appCode) {
        return userDetailsRemote.listPermission(appCode).getData();
    }

    public static void main(String[] args) {
        SysUserDto sysUserDto = new SysUserDto();
        SysPermissionDto sysPermissionDto = new SysPermissionDto();
        sysPermissionDto.setId("1");
        sysPermissionDto.setName("权限1");
        sysPermissionDto.setOperationUrl("/a");
        SysPermissionDto sysPermissionDto2 = new SysPermissionDto();
        sysPermissionDto2.setId("2");
        sysPermissionDto2.setName("权限2");
        sysPermissionDto2.setOperationUrl("/b");
        List<SysPermissionDto> sysPermissionDtoList = new ArrayList<>();
        sysPermissionDtoList.add(sysPermissionDto);
        sysPermissionDtoList.add(sysPermissionDto2);
        sysUserDto.setSysPermissionDtoList(sysPermissionDtoList);
        sysUserDto.setRoleIdSets(new HashSet<String>(Arrays.asList("roleId-1", "roleId-2")));
        sysUserDto.setRoleNameSets(new HashSet<String>(Arrays.asList("roleName-1", "roleName-2")));
        sysUserDto.setRoleTypeSets(new HashSet<Integer>(Arrays.asList(1, 2)));
        sysUserDto.setId("id");
        sysUserDto.setUsername("1");
        sysUserDto.setPassword("1");
        sysUserDto.setNickname("1");
        sysUserDto.setEmail("1");
        sysUserDto.setPhone("1");
        sysUserDto.setUnitName("1");
        System.out.println(JSON.toJSONString(R.ok(sysUserDto)));
        System.out.println(JSON.toJSONString(R.ok(sysPermissionDtoList)));
    }
}
