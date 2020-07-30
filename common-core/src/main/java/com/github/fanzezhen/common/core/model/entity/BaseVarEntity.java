package com.github.fanzezhen.common.core.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 公共Model,将每个表都有的公共字段抽取出来
 *
 * @ MappedSuperclass注解表示不是一个完整的实体类，将不会映射到数据库表，但是它的属性都将映射到其子类的数据库字段中
 */
@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BaseVarEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @TableField(value = "ID")
    @TableId(value = "ID", type = IdType.UUID)
    private String id;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    @Column(name = "CREATE_USER_ID")
    @TableField(value = "CREATE_USER_ID")
    private String createUserId;

    public void init(BaseVarEntity baseVarEntry) {
        this.id = baseVarEntry.getId();
        this.createTime = baseVarEntry.getCreateTime();
        this.createUserId = baseVarEntry.getCreateUserId();
    }

    public BaseVarEntity(String createUserId) {
        this.createUserId = createUserId;
    }
}
