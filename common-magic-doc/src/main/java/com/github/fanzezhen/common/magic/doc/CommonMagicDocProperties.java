package com.github.fanzezhen.common.magic.doc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zezhen.fan
 */
@Data
@ConfigurationProperties(prefix = "com.github.fanzezhen.common.magic.doc")
@SuppressWarnings("unused")
public class CommonMagicDocProperties {
    private String resultJson = "{\"status\":{\"description\":\"响应码：200-正常\",\"type\":\"integer\"},\"message\":{\"description\":\"响应消息\",\"type\":\"string\"}}}";
}
