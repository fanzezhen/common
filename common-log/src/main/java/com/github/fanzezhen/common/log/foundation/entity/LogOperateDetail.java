package com.github.fanzezhen.common.log.foundation.entity;

import com.github.fanzezhen.common.core.model.entity.BaseVarEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Index;
import javax.persistence.Table;

/**
 * <p>
 * 
 * </p>
 *
 * @author fanzezhen
 * @since 2020-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="LogOperateDetail对象", description="操作日志详情")
@Table(indexes = {
        @Index(name = "ix_app_module_type", columnList = "APP_CODE, LOG_ID")
})
public class LogOperateDetail extends BaseVarEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 操作日志Id
     */
    private String logId;

    /**
     * 列字段
     */
    private String tableColumn;

    /**
     * 列名称
     */
    private String columnName;

    /**
     * oldValue
     */
    private String oldValue;

    /**
     * newValue
     */
    private String newValue;

    /**
     * 备注
     */
    private String comment;

    @ApiModelProperty(value = "APP标识")
    private String appCode;

}
