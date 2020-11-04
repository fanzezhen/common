package com.github.fanzezhen.common.core.model.entity;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * 公共Entity,将每个表都有的公共字段抽取出来
 *
 * @ MappedSuperclass注解表示不是一个完整的实体类，将不会映射到数据库表，但是它的属性都将映射到其子类的数据库字段中
 * @author zezhen.fan
 */
@MappedSuperclass
public class BaseEntity implements Serializable {
}
