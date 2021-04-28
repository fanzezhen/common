package com.github.fanzezhen.common.core.model.dto;


import com.github.fanzezhen.common.core.model.entity.BaseVarEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author zezhen.fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysPermissionDto extends BaseVarEntity {
    /**
     * 上级ID
     */
    @Size(max = 50, message = "上级ID长度不能超过50")
    @NotBlank(message = "上级ID不能为空")
    @ApiModelProperty("上级ID")
    private String pid;

    /**
     * icon代码
     */
    @Size(max = 16, message = "icon代码长度不能超过16")
    @NotBlank(message = "icon代码不能为空")
    @ApiModelProperty("icon代码")
    private String icon;

    /**
     * 权限代码
     */
    @Size(max = 50, message = "权限代码长度不能超过50")
    @NotBlank(message = "权限代码不能为空")
    @ApiModelProperty("权限代码")
    private String code;

    /**
     * 名称
     */
    @Size(max = 50, message = "名称长度不能超过50")
    @NotBlank(message = "名称不能为空")
    @ApiModelProperty("名称")
    private String name;

    /**
     * 请求地址
     */
    @Size(max = 255, message = "请求地址长度不能超过255")
    @NotBlank(message = "请求地址不能为空")
    @ApiModelProperty("请求地址")
    private String operationUrl;

    /**
     * 类型（1--菜单；2--按钮）
     */
    @ApiModelProperty("类型（1--菜单；2--按钮）")
    private Integer type;

    /**
     * 排序优先级
     */
    @ApiModelProperty("排序优先级")
    private Integer orderNum;

    /**
     * 所属应用代码
     */
    @Size(max = 50, message = "所属应用代码长度不能超过50")
    @ApiModelProperty("所属应用代码")
    private String appCode;
}
