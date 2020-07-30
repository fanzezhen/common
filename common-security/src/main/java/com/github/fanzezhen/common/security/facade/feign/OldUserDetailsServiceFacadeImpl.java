package com.github.fanzezhen.common.security.facade.feign;

import com.github.fanzezhen.common.core.constant.SecurityConstant;
import com.github.fanzezhen.common.core.enums.auth.RoleEnum;
import com.github.fanzezhen.common.security.facade.OldUserDetailsServiceFacade;
import com.github.fanzezhen.common.security.facade.feign.remote.SysPermissionRemote;
import com.github.fanzezhen.common.security.facade.feign.remote.SysRoleRemote;
import com.github.fanzezhen.common.security.facade.feign.remote.SysUserRemote;
import com.github.fanzezhen.common.security.model.SysUserDetail;
import com.github.fanzezhen.common.security.property.SecurityProjectProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 旧的UserDetailsServiceFacade实现方式
 */
@Slf4j
@Deprecated
public class OldUserDetailsServiceFacadeImpl implements OldUserDetailsServiceFacade {
    @Resource
    private SecurityProjectProperty securityProjectProperty;

    @Resource
    private SysUserRemote sysUserRemote;
    @Resource
    private SysRoleRemote sysRoleRemote;
    @Resource
    private SysPermissionRemote sysPermissionRemote;

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
        sysUserMap = StringUtils.isEmpty(securityProjectProperty.APP_CODE) ? sysUserRemote.loadUserByUsername(username) :
                sysUserRemote.loadUserByUsername(username, securityProjectProperty.APP_CODE);
        if (sysUserMap != null) {
            //生成环境是查询数据库获取username的角色用于后续权限判断（如：张三 admin)
            Set<GrantedAuthority> grantedAuthorities;
            Set<String> grantedAuthorityNameSet = new HashSet<>();
            Set<String> sysRoleIdSet = new HashSet<>();
            Set<String> roleNameSet = new HashSet<>();
            Set<Integer> roleTypeSet = new HashSet<>();

            List<Map<String, Object>> sysRoleMapList;
            sysRoleMapList = StringUtils.isEmpty(securityProjectProperty.APP_CODE) ?
                    sysRoleRemote.listMapByUserId(String.valueOf(sysUserMap.get("id"))) :
                    sysRoleRemote.listMapByUserId(String.valueOf(sysUserMap.get("id")), securityProjectProperty.APP_CODE);
            for (Map<String, Object> sysRoleMap : sysRoleMapList) {
                sysRoleIdSet.add(String.valueOf(sysRoleMap.get("id")));
                roleNameSet.add(String.valueOf(sysRoleMap.get("roleName")));
                roleTypeSet.add((Integer) sysRoleMap.get("roleType"));
            }
            if (!sysRoleIdSet.isEmpty())
                for (Object sysPermissionId : roleTypeSet.contains(RoleEnum.RoleTypeEnum.SPECIAL_ADMIN.getType()) ?   // 超级管理员拥有所有权限
                        sysPermissionRemote.listId() : sysPermissionRemote.listIdByRoleIds(sysRoleIdSet)) {
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
    public List<Map<String, Object>> listMap(String appCode) {
        return sysPermissionRemote.listMapByAppCode(appCode);
    }

    @Override
    public List<Map<String, Object>> listMap() {
        return sysPermissionRemote.listMap();
    }
}
