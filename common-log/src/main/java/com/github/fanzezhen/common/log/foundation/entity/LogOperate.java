package com.github.fanzezhen.common.log.foundation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.github.fanzezhen.common.core.model.entity.BaseVarEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author fanzezhen
 * @since 2021-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="LogOperate对象", description="")
public class LogOperate extends BaseVarEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务Id")
    private String bizId;

    @ApiModelProperty(value = "表名称")
    private String tableName;

    @ApiModelProperty(value = "操作类型")
    private Integer operateType;

    @ApiModelProperty(value = "操作模块")
    private Integer module;

    @ApiModelProperty(value = "IP地址")
    private String ipAddress;

    @ApiModelProperty(value = "设备号")
    private String deviceNum;

    @ApiModelProperty(value = "备注")
    private String comment;

    @ApiModelProperty(value = "操作人名称")
    private String operateUsername;

    @ApiModelProperty(value = "APP标识")
    private String appCode;

}
