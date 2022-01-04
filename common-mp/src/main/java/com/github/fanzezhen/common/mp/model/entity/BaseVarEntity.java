package com.github.fanzezhen.common.mp.model.entity;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import com.github.fanzezhen.common.mp.enums.StatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Index;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

/**
 * 公共Model,将每个表都有的公共字段抽取出来
 *
 * @author zezhen.fan
 * @ MappedSuperclass注解表示不是一个完整的实体类，将不会映射到数据库表，但是它的属性都将映射到其子类的数据库字段中
 */
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(indexes = {
        @Index(name = "ix_basic", columnList = "DEL_FLAG, STATUS")
})
public class BaseVarEntity extends BaseEntity {

    /**
     * 状态（0--正常；1--禁用），默认 0
     */
    @EnumValue
    @Column(name = "STATUS")
    @TableField(value = "STATUS", fill = FieldFill.INSERT)
    @ApiModelProperty("状态（0--正常；1--禁用），默认 0")
    @JSONField(serialzeFeatures = SerializerFeature.WriteEnumUsingToString)
    private StatusEnum status;

    /**
     * 数据版本号
     */
    @Version
    @Column(name = "VERSION")
    @TableField(value = "VERSION", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("数据版本号，默认 0")
    private Integer version;

    public static String[] getFieldNames() {
        return ArrayUtil.append(BaseEntity.getFieldNames(), "status", "version");
    }
}
