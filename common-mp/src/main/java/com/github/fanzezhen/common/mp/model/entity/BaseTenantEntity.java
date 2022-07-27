package com.github.fanzezhen.common.mp.model.entity;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.annotation.TableField;
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
        @Index(name = "ix_del_status", columnList = "DEL_FLAG, STATUS")
})
public class BaseTenantEntity extends BaseEntity {

    /**
     * 租户id
     */
    @ApiModelProperty("租户id")
    @TableField(value = "TENANT_ID")
    @Column(name = "TENANT_ID",columnDefinition = "varchar(50) not null comment '租户id'", nullable = false)
    protected String tenantId;

    public static String[] getFieldNames() {
        return ArrayUtil.append(BaseEntity.getFieldNames(), "TENANT_ID");
    }
}
