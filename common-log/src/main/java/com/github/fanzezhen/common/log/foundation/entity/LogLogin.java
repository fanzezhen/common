package com.github.fanzezhen.common.log.foundation.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.github.fanzezhen.common.core.enums.db.log.LoginLogTypeEnum;
import com.github.fanzezhen.common.core.model.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Index;
import javax.persistence.Table;
import java.math.BigInteger;

/**
 * <p>
 * 登录日志表
 * </p>
 *
 * @author fanzezhen
 * @since 2020-06-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table(indexes = {
        @Index(name = "ix_app_type", columnList = "APP_CODE, LOG_TYPE")
})
public class LogLogin extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 1--登录成功； 2--登录失败； 3--退出登录
     */
    @EnumValue
    @Column(name = "LOG_TYPE")
    @ApiModelProperty(value = "日志类型")
    private LoginLogTypeEnum logType;

    @ApiModelProperty(value = "ip")
    private BigInteger ip;

    @ApiModelProperty(value = "操作系统")
    private String os;

    @ApiModelProperty(value = "浏览器名称")
    private String browserName;

    @ApiModelProperty(value = "浏览器版本")
    private String browserVersion;

    @ApiModelProperty(value = "备注")
    private String remark;

}
