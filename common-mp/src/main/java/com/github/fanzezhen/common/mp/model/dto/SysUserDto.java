package com.github.fanzezhen.common.mp.model.dto;

import com.github.fanzezhen.common.core.annotion.OperateLog;
import com.github.fanzezhen.common.mp.model.SysUserDict;
import com.github.fanzezhen.common.mp.model.entity.BaseTenantEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author zezhen.fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@OperateLog(dictClass = SysUserDict.class, tableName = "sys_user")
public class SysUserDto extends BaseTenantEntity {
    /**
     * 单位名称
     */
    @ApiModelProperty("单位名称")
    private String unitName;

    /**
     * 权限标识集合
     */
    @ApiModelProperty("权限标识集合")
    private List<SysPermissionDto> sysPermissionDtoList;

    /**
     * 角色ID集合
     */
    @ApiModelProperty("角色ID集合")
    private Set<String> roleIdSets;
    /**
     * 角色名称集合
     */
    @ApiModelProperty("角色名称集合")
    private Set<String> roleNameSets;
    /**
     * 角色类型集合
     */
    @ApiModelProperty("角色类型集合")
    private Set<Integer> roleTypeSets;

    // 以下为基本信息
    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String password;

    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    private String nickname;

    /**
     * 头像地址
     */
    @ApiModelProperty("头像地址")
    private String avatar;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String email;

    /**
     * 联系电话
     */
    @ApiModelProperty("联系电话")
    private String phone;

    /**
     * 性别（0--女；1--男；2--未知的性别；3--未说明的性别）
     */
    @ApiModelProperty("性别（0--女；1--男；2--未知的性别；3--未说明的性别）")
    private Integer sex;

    /**
     * 所属部门
     */
    @ApiModelProperty("所属部门")
    private String department;

    /**
     * 最后操作时间
     */
    @ApiModelProperty("最后操作时间")
    private LocalDateTime lastTime;

    /**
     * 所属应用代码
     */
    @ApiModelProperty("所属应用代码")
    private String appCode;
}
