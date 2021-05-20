package com.github.fanzezhen.common.log.foundation.entity;

import com.github.fanzezhen.common.core.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@ApiModel(value="LogOperationDetail对象", description="操作日志详情表")
@Table(indexes = {
        @Index(name = "ix_lid_del", columnList = "log_id, del_flag")
})
@Accessors(chain = true)
public class LogOperationDetail extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "操作日志Id")
    private String logId;

    @ApiModelProperty(value = "列字段")
    private String tableColumn;

    @ApiModelProperty(value = "列名称")
    private String columnName;

    @ApiModelProperty(value = "oldValue")
    private String oldValue;

    @ApiModelProperty(value = "newValue")
    private String newValue;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "APP标识")
    private String appCode;

}
