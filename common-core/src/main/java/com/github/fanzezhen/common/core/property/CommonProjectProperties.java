package com.github.fanzezhen.common.core.property;

import lombok.Data;

/**
 * @author zezhen.fan
 */
@Data
public class CommonProjectProperties {
    private String appCode = "DEMO";
    private String version = "1.0";
    private String basePackage = "com.github.fanzezhen.common";
    private String dtoPackage = "";
    private String title = "DEMO";
    private String description = "示例";
    private String linkMan = "";
    private String linkUrl = "";
    private String linkEmail = "";
    private String license = "Apache License 2.0";
    private String licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0";
}
