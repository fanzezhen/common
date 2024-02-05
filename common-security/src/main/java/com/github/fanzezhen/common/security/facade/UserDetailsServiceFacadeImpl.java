package com.github.fanzezhen.common.security.facade;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import com.github.fanzezhen.common.core.constant.CacheConstant;
import com.github.fanzezhen.common.core.constant.SecurityConstant;
import com.github.fanzezhen.common.core.enums.auth.RoleTypeEnum;
import com.github.fanzezhen.common.core.property.CommonCoreProperties;
import com.github.fanzezhen.common.mp.model.dto.SysPermissionDto;
import com.github.fanzezhen.common.mp.model.dto.SysUserDto;
import com.github.fanzezhen.common.core.model.response.ActionResult;
import com.github.fanzezhen.common.core.model.response.ErrorInfo;
import com.github.fanzezhen.common.security.facade.remote.UserDetailsRemote;
import com.github.fanzezhen.common.security.model.SysUserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zezhen.fan
 */
@Slf4j
@Service
public class UserDetailsServiceFacadeImpl implements UserDetailsServiceFacade {
    @Resource
    private CommonCoreProperties commonCoreProperties;
    @Resource
    private UserDetailsRemote userDetailsRemote;

    @Override
    public UserDetails loadUserDetails(CasAssertionAuthenticationToken casAssertionAuthenticationToken) throws UsernameNotFoundException {
        String username = casAssertionAuthenticationToken.getName();
        log.info("当前的用户名是：" + username);

        SysUserDetail userInfo = loadUserByUsername(username);
        log.info(userInfo.toString());
        return userInfo;
    }

    @Override
    @Cacheable(value = CacheConstant.USER_DETAILS, key = "#username")
    public SysUserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
        //用户，用于判断权限，请注意此用户名和方法参数中的username一致；BCryptPasswordEncoder是用来演示加密使用。
        SysUserDto sysUserDto = userDetailsRemote.loadUserByUsername(username, commonCoreProperties.getAppCode()).getData();
        if (sysUserDto != null && CharSequenceUtil.isNotBlank(sysUserDto.getUsername())) {
            //生成环境是查询数据库获取username的角色用于后续权限判断（如：张三 admin)
            Set<GrantedAuthority> grantedAuthorities;
            Set<String> grantedAuthorityNameSet = new HashSet<>();
            if (!CollUtil.isEmpty(sysUserDto.getRoleTypeSets())) {
                // 判断SPECIAL_ADMIN， 超级管理员拥有所有权限
                for (SysPermissionDto sysPermissionDto :
                        sysUserDto.getRoleTypeSets().contains(RoleTypeEnum.SPECIAL_ADMIN.getType()) ?
                                userDetailsRemote.listPermission(commonCoreProperties.getAppCode()).getData() :
                                sysUserDto.getSysPermissionDtoList()) {
                    grantedAuthorityNameSet.add(SecurityConstant.PERMISSION_PREFIX + sysPermissionDto.getId());
                }
            }
            grantedAuthorityNameSet.addAll(RoleTypeEnum.securityRoleTypeCodeSetByType(sysUserDto.getRoleTypeSets()));
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
    @Cacheable(value = CacheConstant.PERMISSION_DETAILS, key = "#appCode")
    public List<SysPermissionDto> listAllPermissionDto(String appCode) {
        String errMsg = "调用微服务获取权限列表失败：";
        ActionResult<List<SysPermissionDto>> sysPermissionDtoListResult = userDetailsRemote.listPermission(appCode);
        if (sysPermissionDtoListResult != null) {
            if (sysPermissionDtoListResult.isSuccess()) {
                List<SysPermissionDto> data = sysPermissionDtoListResult.getData();
                return data == null ? new ArrayList<>(0) : data;
            } else {
                if (CollUtil.isNotEmpty(sysPermissionDtoListResult.getErrors())) {
                    ErrorInfo errorInfo = sysPermissionDtoListResult.getErrors().get(0);
                    errMsg += errorInfo.getMessage();
                    throw new ServiceException(errorInfo.getCode(), errMsg);
                }
                throw new ServiceException(CoreExceptionEnum.SERVICE_ERROR.getCode(), errMsg);
            }
        } else {
            errMsg += "获取权限列表失败，返回结果为null";
            log.warn(errMsg);
            throw new ServiceException(CoreExceptionEnum.SERVICE_ERROR.getCode(), errMsg);
        }
    }
}
