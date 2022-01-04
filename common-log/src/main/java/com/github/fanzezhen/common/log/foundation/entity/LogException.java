package com.github.fanzezhen.common.log.foundation.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.github.fanzezhen.common.mp.enums.log.ExceptionTypeEnum;
import com.github.fanzezhen.common.mp.model.entity.BaseEntity;
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
        @Index(name = "ix_del_app_time", columnList = "del_flag, app_code, update_time")
})
public class LogException extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 日志类型
     */
    @EnumValue
    @Column(name = "LOG_TYPE")
    @ApiModelProperty(value = "日志类型")
    @JSONField(serialzeFeatures= SerializerFeature.WriteEnumUsingToString)
    private ExceptionTypeEnum logType;

    @ApiModelProperty(value = "资源所属模块")
    private String modular;

    @ApiModelProperty(value = "错误类名")
    private String className;

    @ApiModelProperty(value = "错误说明")
    private String message;

    @ApiModelProperty(value = "错误堆栈")
    private String stackTrace;

    @ApiModelProperty(value = "操作人名称")
    private String operationUsername;

    @ApiModelProperty(value = "应用代码")
    private String appCode;

}
