package com.github.fanzezhen.common.core.model.bean;

import lombok.Data;

/**
 * @author zezhen.fan
 */
@Data
public class SwaggerRequestParameter {
    private String name;
    private int parameterIndex;
    private String description;
    private Boolean required;
    private Boolean deprecated;
    private Boolean hidden;
    private Integer precedence;
}
