package com.github.fanzezhen.common.exception.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zezhen.fan
 */
@Component
public class ResponseProperty {
    @Value("${response.json.flag:false}")
    public boolean jsonFlag;
}
