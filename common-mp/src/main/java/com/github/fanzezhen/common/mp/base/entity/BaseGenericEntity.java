package com.github.fanzezhen.common.mp.base.entity;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.github.fanzezhen.common.mp.enums.DelFlagEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 公共Model,将每个表都有的公共字段抽取出来
 *
 * @author zezhen.fan
 * @ MappedSuperclass注解表示不是一个完整的实体类，将不会映射到数据库表，但是它的属性都将映射到其子类的数据库字段中
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(indexes = {
        @Index(name = "ix_del_status", columnList = "DEL_FLAG, STATUS")
})
public abstract class BaseGenericEntity<K extends Serializable> extends BaseEntity<K> {

    /**
     * 删除标识（1-已删除；0-未删除），默认 0
     */
    @EnumValue
    @TableLogic
    @Column(name = "DEL_FLAG")
    @TableField(value = "DEL_FLAG", fill = FieldFill.INSERT)
    @Schema(name = "删除标识（1-已删除；0-未删除），默认 0")
    @JSONField(serialzeFeatures = SerializerFeature.WriteEnumUsingToString)
    private DelFlagEnum delFlag;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    @TableField(value = "UPDATE_TIME", fill = FieldFill.INSERT_UPDATE)
    @Schema(name = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 更新者ID
     */
    @Column(name = "UPDATE_USER_ID")
    @TableField(value = "UPDATE_USER_ID", fill = FieldFill.INSERT_UPDATE)
    @Schema(name = "更新者ID")
    private String updateUserId;

    public boolean isDeleted() {
        return delFlag != null && Objects.equals(DelFlagEnum.DELETED.code, delFlag.code);
    }

    public static String[] getFieldNames() {
        return ArrayUtil.append(BaseEntity.getFieldNames(), "update_time", "update_user_id", "del_flag");
    }
}
