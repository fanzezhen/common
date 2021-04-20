package com.github.fanzezhen.common.core.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.github.fanzezhen.common.core.enums.db.DelFlagEnum;
import com.github.fanzezhen.common.core.enums.db.StatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;

import javax.persistence.Column;
import javax.persistence.Index;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 公共Model,将每个表都有的公共字段抽取出来
 *
 * @author zezhen.fan
 * @ MappedSuperclass注解表示不是一个完整的实体类，将不会映射到数据库表，但是它的属性都将映射到其子类的数据库字段中
 */
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(indexes = {
        @Index(name = "ix_basic", columnList = "DEL_FLAG, STATUS")
})
public class BaseVarAloneEntity extends BaseVarEntity {
    /**
     * 删除标识（1-已删除；0-未删除），默认 0
     */
    @EnumValue
    @TableLogic
    @Column(name = "DEL_FLAG")
    @TableField(value = "DEL_FLAG", fill = FieldFill.INSERT)
    @ApiModelProperty("删除标识（1-已删除；0-未删除），默认 0")
    private DelFlagEnum delFlag;
    /**
     * 状态（0--正常；1--禁用），默认 0
     */
    @EnumValue
    @Column(name = "STATUS")
    @TableField(value = "STATUS", fill = FieldFill.INSERT)
    @ApiModelProperty("状态（0--正常；1--禁用），默认 0")
    private StatusEnum status;
    /**
     * 数据版本号
     */
    @Version
    @Column(name = "VERSION")
    @TableField(value = "VERSION", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("数据版本号，默认 0")
    private Integer version;
    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    @TableField(value = "UPDATE_TIME", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
    /**
     * 更新者ID
     */
    @Column(name = "UPDATE_USER_ID")
    @TableField(value = "UPDATE_USER_ID", fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("更新者ID")
    private String updateUserId;

    public void init(BaseVarAloneEntity baseAlonePo) {
        this.status = baseAlonePo.getStatus();
        this.delFlag = baseAlonePo.getDelFlag();
        this.updateTime = baseAlonePo.getUpdateTime();
        this.updateUserId = baseAlonePo.getUpdateUserId();
        super.init(baseAlonePo);
    }

    public static String[] getFieldNames() {
        return ArrayUtils.addAll(BaseVarEntity.getFieldNames(),
                "status", "del_flag", "version", "update_time", "update_user_id");
    }
}
