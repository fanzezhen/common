package com.github.fanzezhen.common.core.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * 公共Model,将每个表都有的公共字段抽取出来
 *
 * @ MappedSuperclass注解表示不是一个完整的实体类，将不会映射到数据库表，但是它的属性都将映射到其子类的数据库字段中
 */
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BaseVarAloneEntity extends BaseVarEntity {
    /**
     * 状态（0--正常；1--禁用）
     */
    @Column(name = "STATUS")
    @TableField(value = "STATUS")
    private Integer status; //0--正常；1--禁用
    /**
     * 删除标识（1-已删除；0-未删除）
     */
    @Column(name = "DEL_FLAG")
    @TableField(value = "DEL_FLAG")
    private Integer delFlag; //1-已删除；0-未删除
    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    @TableField(value = "UPDATE_TIME")
    private LocalDateTime updateTime;
    /**
     * 更新者ID
     */
    @Column(name = "UPDATE_USER_ID")
    @TableField(value = "UPDATE_USER_ID")
    private String updateUserId;

    public void init(BaseVarAloneEntity baseAlonePo) {
        this.status = baseAlonePo.getStatus();
        this.delFlag = baseAlonePo.getDelFlag();
        this.updateTime = baseAlonePo.getUpdateTime();
        this.updateUserId = baseAlonePo.getUpdateUserId();
        super.init(baseAlonePo);
    }
}
