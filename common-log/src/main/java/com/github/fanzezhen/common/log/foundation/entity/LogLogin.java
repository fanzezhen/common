package com.github.fanzezhen.common.log.foundation.entity;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.github.fanzezhen.common.core.enums.db.log.LoginLogTypeEnum;
import com.github.fanzezhen.common.core.model.entity.BaseEntity;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.IPAddress;

import javax.persistence.Column;
import javax.persistence.Index;
import javax.persistence.Table;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>
 * 登录日志表
 * </p>
 *
 * @author fanzezhen
 * @since 2020-06-11
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table(indexes = {
        @Index(name = "ix_username_type_time", columnList = "username, log_type, del_flag, update_time")
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
    @JSONField(serialzeFeatures = SerializerFeature.WriteEnumUsingToString)
    private LoginLogTypeEnum logType;

    @ApiModelProperty(value = "ip")
    private byte[] ipBytes;

    @ApiModelProperty(value = "操作系统")
    private String os;

    @ApiModelProperty(value = "浏览器名称")
    private String browserName;

    @ApiModelProperty(value = "浏览器版本")
    private String browserVersion;

    @ApiModelProperty(value = "备注")
    private String remark;

    public void setIp(String ip) {
        if (StrUtil.isBlank(ip)) {
            return;
        }
        try {
            if (IPAddress.isValid(ip)) {
                this.ipBytes = InetAddress.getByName(ip).getAddress();
            }
        } catch (UnknownHostException e) {
            log.warn("IP地址解析失败：{}", ip, e);
        }
    }

    public String getIp() {
        if (ipBytes != null) {
            int ipv4IntLength = 4;
            int ipv4LongLength = 8;
            int ipv6Length = 16;
            if (ipBytes.length == ipv4IntLength) {
                return NetUtil.longToIpv4(Ints.fromByteArray(ipBytes));
            }
            if (ipBytes.length == ipv4LongLength) {
                return NetUtil.longToIpv4(Longs.fromByteArray(ipBytes));
            }
            if (ipBytes.length == ipv6Length) {
                return NetUtil.bigIntegerToIPv6(new BigInteger(ipBytes));
            }
        }
        return null;
    }

    public void setIpv4(String ipv4) {
        if (StrUtil.isBlank(ipv4)) {
            return;
        }
        try {
            this.ipBytes = InetAddress.getByName(ipv4).getAddress();
        } catch (Throwable throwable) {
            log.warn("setIpv4 failed: {}", ipv4, throwable);
        }
    }

    public String getIpv4() {
        if (ipBytes != null) {
            int ipv4IntLength = 4;
            int ipv4LongLength = 8;
            if (ipBytes.length == ipv4IntLength) {
                return NetUtil.longToIpv4(Ints.fromByteArray(ipBytes));
            }
            if (ipBytes.length == ipv4LongLength) {
                return NetUtil.longToIpv4(Longs.fromByteArray(ipBytes));
            }
        }
        return null;
    }

    public void setIpv6(String ipv6) {
        if (StrUtil.isBlank(ipv6)) {
            return;
        }
        try {
            this.ipBytes = InetAddress.getByName(ipv6).getAddress();
        } catch (Throwable throwable) {
            log.warn("setIpv6 failed: {}", ipv6, throwable);
        }
    }

    public String getIpv6() {
        if (ipBytes != null) {
            int ipv6Length = 16;
            if (ipBytes.length == ipv6Length) {
                return NetUtil.bigIntegerToIPv6(new BigInteger(ipBytes));
            }
        }
        return null;
    }

}
