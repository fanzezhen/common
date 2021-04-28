package com.github.fanzezhen.common.core.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.annotation.Version;
import com.github.fanzezhen.common.core.enums.db.DelFlagEnum;
import com.github.fanzezhen.common.core.enums.db.StatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公共Model,将每个表都有的公共字段抽取出来
 *
 * @author zezhen.fan
 * @ MappedSuperclass注解表示不是一个完整的实体类，将不会映射到数据库表，但是它的属性都将映射到其子类的数据库字段中
 */
@Data
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @TableField(value = "ID")
    @TableId(value = "ID", type = IdType.ASSIGN_UUID)
    @ApiModelProperty("主键ID")
    private String id;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    @Column(name = "CREATE_USER_ID")
    @TableField(value = "CREATE_USER_ID", fill = FieldFill.INSERT)
    @ApiModelProperty("创建人ID")
    private String createUserId;

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

    public void init(BaseEntity baseVarEntry) {
        this.id = baseVarEntry.getId();
        this.createTime = baseVarEntry.getCreateTime();
        this.createUserId = baseVarEntry.getCreateUserId();
    }

    public static String[] getFieldNames() {
        return new String[]{"id", "create_time", "create_user_id", "update_time", "update_user_id", "del_flag"};
    }

}
