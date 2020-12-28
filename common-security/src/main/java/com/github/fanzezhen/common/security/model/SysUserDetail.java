package com.github.fanzezhen.common.security.model;

import com.github.fanzezhen.common.core.util.BeanConverterUtil;
import com.github.fanzezhen.common.core.model.dto.SysUserDto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author zezhen.fan
 */
@Slf4j
@Getter
@Setter
public class SysUserDetail extends User implements CredentialsContainer {
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 性别（0--女；1--男；2--未知的性别；3--未说明的性别）
     */
    private Integer sex;

    /**
     * 所属部门
     */
    private String department;

    /**
     * 是否删除（0--否；1--是）
     */
    private Integer delFlag;

    /**
     * 状态（0--正常；1--停用）
     */
    private Integer status;

    /**
     * 最后操作时间
     */
    private LocalDateTime lastTime;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 填表人ID
     */
    private String createUserId;

    /**
     * 最后更新人ID
     */
    private String updateUserId;

    /**
     * 所属应用代码
     */
    private String appCode;

    private Set<GrantedAuthority> authorities;
    /**
     * 默认返回的是false，翻译成人话就是：是否不上锁，否，即上锁。
     */
    private boolean accountNonLocked = true;
    /**
     * 用户是否被有效，false时为禁用状态
     */
    private boolean enabled = true;
    /**
     * User account has expired
     */
    private boolean accountNonExpired = true;
    /**
     * User credentials have expired
     */
    private boolean credentialsNonExpired = true;

    private Collection<String> roleNames;
    private Collection<String> roleIds;
    private Collection<Integer> roleTypes;

    private String oldPassword;

    public SysUserDetail(SysUserDto sysUserDto, Collection<? extends GrantedAuthority> authorities) {
        super(sysUserDto.getUsername(), sysUserDto.getPassword(), authorities);
        if (this.authorities == null) {
            log.warn("user：{} authorities 为 null", sysUserDto.getUsername());
            this.authorities = new HashSet<>();
        }
        BeanConverterUtil.copy(sysUserDto, this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("Username: ").append(getUsername()).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Enabled: ").append(this.enabled).append("; ");
        sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
        sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired).append("; ");
        sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");
        if (this.authorities != null && !this.authorities.isEmpty()) {
            sb.append("Granted Authorities: ");
            boolean first = true;

            for (GrantedAuthority auth : this.authorities) {
                if (!first) {
                    sb.append(",");
                }

                first = false;
                sb.append(auth);
            }
        } else {
            sb.append("Not granted any authorities");
        }

        return sb.toString();
    }
}
