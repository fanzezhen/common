package com.github.fanzezhen.common.log.foundation.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.github.fanzezhen.common.core.enums.db.log.ExceptionTypeEnum;
import com.github.fanzezhen.common.core.model.entity.BaseVarEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * <p>
 * 异常日志表
 * </p>
 *
 * @author fanzezhen
 * @since 2020-06-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table(indexes = {
        @Index(name = "ix_app_module_type", columnList = "APP_CODE, MODULE, LOG_TYPE")
})
public class LogException extends BaseVarEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 日志类型
     */
    @EnumValue
    @Column(name = "LOG_TYPE")
    @ApiModelProperty(value = "日志类型")
    private ExceptionTypeEnum logType;

    /**
     * 资源所属模块
     */
    private String modular;

    /**
     * 错误类名
     */
    private String className;

    /**
     * 错误说明
     */
    private String message;

    /**
     * 错误堆栈
     */
    private String stackTrace;

    /**
     * 应用代码
     */
    private String appCode;

}
