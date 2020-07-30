package com.github.fanzezhen.common.core.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 操作日志表
 * </p>
 *
 * @author fanzezhen
 * @since 2020-07-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class LogOperation extends BaseVarEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 日志类型（1-增；2-删；3-改）
     */
    private Integer logType;

    /**
     * 资源所属模块
     */
    private String modular;

    /**
     * 资源ID
     */
    private String resourceId;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 应用代码
     */
    private String appCode;


}
