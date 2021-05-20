package com.github.fanzezhen.common.log.model.dto;

import cn.hutool.core.util.StrUtil;
import com.github.fanzezhen.common.log.foundation.entity.LogLogin;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author zezhen.fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class LogLoginDto extends LogLogin {
    private String ip;
    private String ipv4;
    private String ipv6;

    public void clearIpAddress() {
        ip = null;
        ipv4 = null;
        ipv6 = null;
    }

    @Override
    public String getIp() {
        if (StrUtil.isBlank(ip)) {
            setIp(super.getIp());
        }
        return ip;
    }

    @Override
    public String getIpv4() {
        if (StrUtil.isBlank(ipv4)) {
            setIpv4(super.getIpv4());
        }
        return ipv4;
    }

    @Override
    public String getIpv6() {
        if (StrUtil.isBlank(ipv6)) {
            setIpv6(super.getIpv6());
        }
        return ipv6;
    }

    @Override
    public void setIp(String ip) {
        super.setIp(ip);
        this.ip = ip;
    }

    @Override
    public void setIpv4(String ipv4) {
        super.setIpv4(ipv4);
        this.ipv4 = ipv4;
    }

    @Override
    public void setIpv6(String ipv6) {
        super.setIpv6(ipv6);
        this.ipv6 = ipv6;
    }

}
