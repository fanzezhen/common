package com.github.fanzezhen.common.core.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 菜单、按钮表
 * </p>
 *
 * @author fanzezhen
 * @since 2020-07-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysPermission extends BaseVarAloneEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 上级ID
     */
    private String pid;

    /**
     * icon
     */
    private String icon;

    /**
     * 权限代码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 请求地址
     */
    private String operationUrl;

    /**
     * 是否为菜单（1--菜单；2--按钮）
     */
    private Integer type;

    /**
     * 排序优先级
     */
    private Integer orderNum;

    /**
     * 所属应用代码
     */
    private String appCode;


}
