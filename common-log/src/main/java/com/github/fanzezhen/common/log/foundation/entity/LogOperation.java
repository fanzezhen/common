package com.github.fanzezhen.common.log.foundation.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.github.fanzezhen.common.mp.enums.log.OperationLogTypeEnum;
import com.github.fanzezhen.common.mp.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Index;
import javax.persistence.Table;

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
@ApiModel(value="LogOperation对象", description="操作日志表")
@Table(indexes = {
        @Index(name = "ix_del_app_time", columnList = "del_flag, app_code, update_time")
})
@Accessors(chain = true)
public class LogOperation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务Id")
    private String bizId;

    @ApiModelProperty(value = "表名称")
    private String tableName;

    @EnumValue
    @Column(name = "OPERATE_TYPE")
    @ApiModelProperty(value = "操作类型")
    @JSONField(serialzeFeatures= SerializerFeature.WriteEnumUsingToString)
    private OperationLogTypeEnum operationType;

    @ApiModelProperty(value = "操作模块")
    private String module;

    @ApiModelProperty(value = "IP地址")
    private String ipAddress;

    @ApiModelProperty(value = "设备号")
    private String deviceNum;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "操作人名称")
    private String operationUsername;

    @ApiModelProperty(value = "APP标识")
    private String appCode;

}
