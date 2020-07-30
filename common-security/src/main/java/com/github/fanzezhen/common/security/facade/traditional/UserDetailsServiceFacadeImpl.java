package com.github.fanzezhen.common.security.facade.traditional;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.fanzezhen.common.core.constant.SecurityConstant;
import com.github.fanzezhen.common.core.enums.auth.RoleEnum;
import com.github.fanzezhen.common.core.model.dto.SysPermissionDto;
import com.github.fanzezhen.common.security.facade.UserDetailsServiceFacade;
import com.github.fanzezhen.common.security.model.SysUserDetail;
import com.github.fanzezhen.common.security.property.SecurityProjectProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class UserDetailsServiceFacadeImpl implements UserDetailsServiceFacade {
    @Resource
    private SecurityProjectProperty securityProjectProperty;

    @Resource(name = "sysUserServiceImpl")
    private IService sysUserService;
    @Resource(name = "sysRoleServiceImpl")
    private IService sysRoleService;
    @Resource(name = "sysPermissionServiceImpl")
    private IService sysPermissionService;
    @Resource(name = "sysRolePermissionServiceImpl")
    private IService sysRolePermissionService;


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
    public SysUserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
        //用户，用于判断权限，请注意此用户名和方法参数中的username一致；BCryptPasswordEncoder是用来演示加密使用。
        Map<String, Object> sysUserMap;
        sysUserMap = sysUserService.getMap(
                StringUtils.isEmpty(securityProjectProperty.APP_CODE) ?
                        new QueryWrapper<>().eq("username", username) :
                        new QueryWrapper<>().eq("username", username).eq("app_code", securityProjectProperty.APP_CODE));
        if (sysUserMap != null) {
            //生成环境是查询数据库获取username的角色用于后续权限判断（如：张三 admin)
            Set<GrantedAuthority> grantedAuthorities;
            Set<String> grantedAuthorityNameSet = new HashSet<>();
            Set<String> sysRoleIdSet = new HashSet<>();
            Set<String> roleNameSet = new HashSet<>();
            Set<Integer> roleTypeSet = new HashSet<>();

            List<Map<String, Object>> sysRoleMapList;
            sysRoleMapList = sysRoleService.listMaps(
                    StringUtils.isEmpty(securityProjectProperty.APP_CODE) ?
                            new QueryWrapper<>().inSql("id", "select role_id from sys_user_role where user_id = '" + sysUserMap.get("id") + "'") :
                            new QueryWrapper<>()
                                    .inSql("id", "select role_id from sys_user_role where user_id = '" + sysUserMap.get("id") + "'")
                                    .eq("app_code", securityProjectProperty.APP_CODE));
            for (Map<String, Object> sysRoleMap : sysRoleMapList) {
                sysRoleIdSet.add(String.valueOf(sysRoleMap.get("id")));
                roleNameSet.add(String.valueOf(sysRoleMap.get("roleName")));
                roleTypeSet.add((Integer) sysRoleMap.get("roleType"));
            }
            if (!sysRoleIdSet.isEmpty())
                for (Object sysPermissionId : sysRolePermissionService.listObjs(
                        roleTypeSet.contains(RoleEnum.RoleTypeEnum.SPECIAL_ADMIN.getType()) ?   // 超级管理员拥有所有权限
                                new QueryWrapper<>().select("permission_id") :
                                new QueryWrapper<>().select("permission_id").in("role_id", sysRoleIdSet)
                )) {
                    grantedAuthorityNameSet.add(SecurityConstant.PERMISSION_PREFIX + sysPermissionId);
                }
            grantedAuthorityNameSet.addAll(RoleEnum.RoleTypeEnum.securityRoleTypeCodeSetByType(roleTypeSet));
            //1：此处将权限信息添加到 GrantedAuthority 对象中，在后面进行全权限验证时会使用GrantedAuthority 对象。
            grantedAuthorities = new HashSet<>(AuthorityUtils.commaSeparatedStringToAuthorityList(
                    org.apache.tomcat.util.buf.StringUtils.join(grantedAuthorityNameSet, ',')));
            SysUserDetail sysUserDetail = new SysUserDetail(sysUserMap, grantedAuthorities);
            sysUserDetail.setRoleIds(sysRoleIdSet);
            sysUserDetail.setRoleNames(roleNameSet);
            sysUserDetail.setRoleTypes(roleTypeSet);
            return sysUserDetail;
        } else {
            throw new UsernameNotFoundException("user: " + username + " do not exist!");
        }

    }

    @Override
    public List<SysPermissionDto> listAllPermissionDto(String appCode) {
        List<SysPermissionDto> sysPermissionDtoList = new ArrayList<>();
        List<Map<String, Object>> permissionMapList = sysPermissionService.listMaps(new QueryWrapper<>().eq("app_code", securityProjectProperty.APP_CODE));
        if (permissionMapList == null) return sysPermissionDtoList;
        for (Map<String, Object> map : permissionMapList)
            sysPermissionDtoList.add(JSON.parseObject(JSON.toJSONString(map), SysPermissionDto.class));
        return sysPermissionDtoList;
    }
}
