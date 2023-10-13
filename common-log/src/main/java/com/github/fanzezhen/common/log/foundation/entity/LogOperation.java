package com.github.fanzezhen.common.log.foundation.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.github.fanzezhen.common.mp.enums.log.OperationLogTypeEnum;
import com.github.fanzezhen.common.mp.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name="LogOperation对象", description="操作日志表")
@Table(indexes = {
        @Index(name = "ix_del_app_time", columnList = "del_flag, app_code, update_time")
})
@Accessors(chain = true)
public class LogOperation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(name = "业务Id")
    private String bizId;

    @Schema(name = "表名称")
    private String tableName;

    @EnumValue
    @Column(name = "OPERATE_TYPE")
    @Schema(name = "操作类型")
    @JSONField(serialzeFeatures= SerializerFeature.WriteEnumUsingToString)
    private OperationLogTypeEnum operationType;

    @Schema(name = "操作模块")
    private String module;

    @Schema(name = "IP地址")
    private String ipAddress;

    @Schema(name = "设备号")
    private String deviceNum;

    @Schema(name = "备注")
    private String remark;

    @Schema(name = "操作人名称")
    private String operationUsername;

    @Schema(name = "APP标识")
    private String appCode;

}
